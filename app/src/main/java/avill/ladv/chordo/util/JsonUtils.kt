package avill.ladv.chordo.util

import org.json.JSONObject
import java.util.ArrayList

/**
 * Utility functions for JSON validation and repairing.
 */

/**
 * Checks if a string is a valid JSON object.
 */
fun String.isValidJson(): Boolean {
    return try {
        JSONObject(this)
        true
    } catch (e: Exception) {
        false
    }
}

/**
 * Attempts to repair a malformed JSON string by splitting it into valid parts.
 * This is useful for legacy streams that might concatenate multiple JSON objects incorrectly.
 */
fun String.fixMalformedJson(): String {
    val parts = this.split("\\},\\{".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    
    return if (parts.size > 1) {
        val jsonResult = ArrayList<String>()
        for (i in parts.indices) {
            when {
                i == 0 -> {
                    val candidate = parts[i] + "}"
                    if (candidate.isValidJson()) jsonResult.add(candidate)
                }
                i == parts.size - 1 -> {
                    val candidate = "{" + parts[i]
                    if (candidate.isValidJson()) jsonResult.add(candidate)
                }
                else -> {
                    val candidate = "{" + parts[i] + "}"
                    if (candidate.isValidJson()) jsonResult.add(candidate)
                }
            }
        }
        
        val result = StringBuilder()
        for (s in jsonResult) {
            result.append(s).append(",")
        }
        
        if (jsonResult.size > 1) {
            result.deleteCharAt(result.length - 1)
        }
        result.toString()
    } else {
        this
    }
}
