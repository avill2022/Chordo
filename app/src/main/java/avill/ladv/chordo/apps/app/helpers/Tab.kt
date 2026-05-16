package avill.ladv.chordo.apps.app.helpers
class Tab(val index: Int, val content: String)

// Function 1: Extract complete guitar tab blocks
fun extractTabs(text: String,r:Int,count:Int = 0): List<Tab> {
    val tabs = mutableListOf<Tab>()
    // Pattern to match a complete tab block with all 6 strings
    var regex = Regex(
        "(?:e\\|.*?\\|\\s*\\n)" +      // e string
                "(?:B\\|.*?\\|\\s*\\n)" +      // B string
                "(?:G\\|.*?\\|\\s*\\n)" +      // G string
                "(?:D\\|.*?\\|\\s*\\n)" +      // D string
                "(?:A\\|.*?\\|\\s*\\n)" +      // A string
                "(?:E\\|.*?\\|)",              // E string (last one, no newline at end)
        RegexOption.DOT_MATCHES_ALL
    )
    if(r == 1)
        regex = Regex(
            "(?:e\\|.*?\\|\\s*\\n)" +      // e string
                    "(?:b\\|.*?\\|\\s*\\n)" +      // B string
                    "(?:g\\|.*?\\|\\s*\\n)" +      // G string
                    "(?:d\\|.*?\\|\\s*\\n)" +      // D string
                    "(?:a\\|.*?\\|\\s*\\n)" +      // A string
                    "(?:E\\|.*?\\|)",              // E string (last one, no newline at end)
            RegexOption.DOT_MATCHES_ALL
        )
    if(r == 2)
        regex = Regex(
            "(?:e\\|.*?\\|\\s*\\n)" +      // e string
                    "(?:b\\|.*?\\|\\s*\\n)" +      // B string
                    "(?:g\\|.*?\\|\\s*\\n)" +      // G string
                    "(?:d\\|.*?\\|\\s*\\n)" +      // D string
                    "(?:a\\|.*?\\|\\s*\\n)" +      // A string
                    "(?:e\\|.*?\\|)",              // E string (last one, no newline at end)
            RegexOption.DOT_MATCHES_ALL
        )

    var tabCount = count
    return regex.findAll(text).map { matchResult ->
        tabCount++
        Tab(tabCount, matchResult.value.trim())
    }.toList()
}
// Alternative more flexible version (handles partial tabs)
fun extractTabsFlexible(text: String): List<Tab> {
    val tabs = mutableListOf<Tab>()
    // Pattern to match tab lines that start with string indicators
    val linePattern = Regex("^[eBGDAE]\\|.*?\\|$", RegexOption.MULTILINE)
    var tabCount = 0
    var currentTab = StringBuilder()

    text.lines().forEach { line ->
        if (line.matches(linePattern)) {
            currentTab.append(line).append("\n")
        } else if (currentTab.isNotEmpty()) {
            // End of tab block
            tabCount++
            tabs.add(Tab(tabCount, currentTab.toString().trim()))
            currentTab.clear()
        }
    }

    // Add last tab if exists
    if (currentTab.isNotEmpty()) {
        tabCount++
        tabs.add(Tab(tabCount, currentTab.toString().trim()))
    }

    return tabs
}

// Function 2: Replace complete tab blocks with [tab-index]
fun replaceTabsWithPlaceholders(text: String,r:Int): String {
    var regex1 = Regex(
        "(?:e\\|.*?\\|\\s*\\n)" +
                "(?:B\\|.*?\\|\\s*\\n)" +
                "(?:G\\|.*?\\|\\s*\\n)" +
                "(?:D\\|.*?\\|\\s*\\n)" +
                "(?:A\\|.*?\\|\\s*\\n)" +
                "(?:E\\|.*?\\|)",
        RegexOption.DOT_MATCHES_ALL
    )
     if(r==2)
        regex1 = Regex(
            "(?:e\\|.*?\\|\\s*\\n)" +
                    "(?:b\\|.*?\\|\\s*\\n)" +
                    "(?:g\\|.*?\\|\\s*\\n)" +
                    "(?:d\\|.*?\\|\\s*\\n)" +
                    "(?:a\\|.*?\\|\\s*\\n)" +
                    "(?:E\\|.*?\\|)",
            RegexOption.DOT_MATCHES_ALL
        )
    if(r==3)
        regex1 = Regex(
            "(?:e\\|.*?\\|\\s*\\n)" +
                    "(?:b\\|.*?\\|\\s*\\n)" +
                    "(?:g\\|.*?\\|\\s*\\n)" +
                    "(?:d\\|.*?\\|\\s*\\n)" +
                    "(?:a\\|.*?\\|\\s*\\n)" +
                    "(?:e\\|.*?\\|)",
            RegexOption.DOT_MATCHES_ALL
        )

    var tabCount = 0

    var rege =  regex1.replace(text) {
        tabCount++
        "[tab-$tabCount]"
    }
    return rege
}

// More flexible replacement (handles incomplete or variable-length tabs)
fun replaceTabsFlexible(text: String): String {
    val lines = text.lines()
    val linePattern = Regex("^[eBGDAE]\\|.*?\\|$")
    val result = StringBuilder()
    var tabCount = 0
    var inTab = false
    var currentTabLines = mutableListOf<String>()

    for (line in lines) {
        if (line.matches(linePattern)) {
            if (!inTab) {
                inTab = true
                currentTabLines.clear()
            }
            currentTabLines.add(line)
        } else {
            if (inTab && currentTabLines.isNotEmpty()) {
                // Check if we have a complete tab (all 6 strings)
                if (currentTabLines.size >= 6) {
                    tabCount++
                    result.append("[tab-$tabCount]")
                } else {
                    // If incomplete tab, preserve original content
                    currentTabLines.forEach { result.append(it).append("\n") }
                }
                inTab = false
                currentTabLines.clear()
            }
            result.append(line).append("\n")
        }
    }

    // Handle trailing tab
    if (inTab && currentTabLines.isNotEmpty()) {
        if (currentTabLines.size >= 6) {
            tabCount++
            result.append("[tab-$tabCount]")
        } else {
            currentTabLines.forEach { result.append(it).append("\n") }
        }
    }

    return result.toString().trim()
}

// Example usage
fun main() {
    val text = """
        Here's a guitar tab:
        
        e|-----------------|-----------------|
        B|-----------------|-----------------|
        G|-----------------|-----------------|
        D|-----------------|-----------------|
        A|-----2-4-2-------|-----0-2-0-------|
        E|---0-------0-2---|---0-------0-2---|
        
        Then some more text here.
        
        e|-----0-------0-----|-----------------|
        B|---1---1-------1---|-----1-----------|
        G|-2-----------2---2-|---2---2---------|
        D|-------------------|-0-------0-------|
        A|-------------------|-----------------|
        E|-------------------|-----------------|
        
        End of example.
    """.trimIndent()

    // Test Function 1
    println("=== extractTabs (strict) ===")
    val tabs = extractTabs(text,0)
    tabs.forEach { tab ->
        println("Tab ${tab.index}:")
        println(tab.content)
        println()
    }

    // Test Function 1 (flexible)
    println("\n=== extractTabsFlexible ===")
    val flexibleTabs = extractTabsFlexible(text)
    flexibleTabs.forEach { tab ->
        println("Tab ${tab.index}:")
        println(tab.content)
        println()
    }

    // Test Function 2
    println("\n=== replaceTabsWithPlaceholders ===")
    val result = replaceTabsWithPlaceholders(text,0)
    println(result)

    // Test Function 2 (flexible)
    println("\n=== replaceTabsFlexible ===")
    val flexibleResult = replaceTabsFlexible(text)
    println(flexibleResult)
}