package avill.ladv.chordo.util.tools

import android.util.Patterns
class ValidationResult (
    val successful:Boolean,
    val errorMessage:String? = null
)
object MyValidationManager {
    fun validateEmail(email:String): ValidationResult {
        if(email.isBlank()){
            return ValidationResult(
                successful = false,
                errorMessage = "The email can't be blank")
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return ValidationResult(
                successful = false,
                errorMessage = "That's not a valid email"
            )
        }
        return ValidationResult(successful = true)
    }
    fun validatePassword(password:String): ValidationResult {
        if(password.length<8){
            return ValidationResult(
                successful = false,
                errorMessage = "The password needs to consist of at least 8 characters")
        }
        val containsLettersAndDigits = password.any {
            it.isDigit()
        } && password.any{it.isLetter()
        }
        if(!containsLettersAndDigits){
            return ValidationResult(
                successful = false,
                errorMessage = "The password needs to contain at least on e letter and digit"
            )
        }
        return ValidationResult(successful = true)
    }
    fun validateRepeatedPassword(password:String,repeatedPassword:String): ValidationResult {
        if(password != repeatedPassword){
            return ValidationResult(
                successful = false,
                errorMessage = "The passwords don't match")
        }
        return ValidationResult(successful = true)
    }
    fun validateTerms(acceptedTerms:Boolean): ValidationResult {
        if(!acceptedTerms){
            return ValidationResult(
                successful = false,
                errorMessage = "Please accept the terms")
        }
        return ValidationResult(successful = true)
    }
    fun validateInputFloatFormat(input:String):String{
        val containsDot = Regex("^.*\\..*$").matches(input)
        if(containsDot){
            val parts = input.split(".")
            var newChar = ""
            if(parts[0].length>=2){
                newChar = parts[0].removeRange(0,1)+"."
            }else{
                newChar = parts[0]  +"."
            }
            if(parts[1].length>=2){
                newChar += parts[1].removeRange(0,1)
            }else{
                newChar += parts[1]
            }
            return newChar
        }else {
            if(input.length>=2){
                val newChar = input.removeRange(0,1)
                return newChar
            }else
                return input
        }
    }
}