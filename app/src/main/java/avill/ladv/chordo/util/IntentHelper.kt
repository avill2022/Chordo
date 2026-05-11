package avill.ladv.chordo.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IntentHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Creates an intent to pick an image from the gallery.
     */
    fun getPickImageIntent(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
    }

    /**
     * Creates an intent to pick a video from the gallery.
     */
    fun getPickVideoIntent(): Intent {
        return Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI).apply {
            type = "video/*"
        }
    }

    /**
     * Creates an intent to pick any file.
     */
    fun getPickFileIntent(mimeType: String = "*/*"): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            type = mimeType
            addCategory(Intent.CATEGORY_OPENABLE)
        }
    }

    /**
     * Creates an intent to capture a photo using the camera.
     */
    fun getCapturePhotoIntent(): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    }

    /**
     * Creates an intent to capture a video using the camera.
     */
    fun getCaptureVideoIntent(): Intent {
        return Intent(MediaStore.ACTION_VIDEO_CAPTURE)
    }

    /**
     * Creates an intent to pick a contact.
     */
    fun getPickContactIntent(): Intent {
        return Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
    }

    /**
     * Creates an intent to send an email.
     */
    fun getSendEmailIntent(
        to: Array<String>,
        subject: String? = null,
        body: String? = null
    ): Intent {
        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, to)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
    }

    /**
     * Creates an intent to open WhatsApp with a specific message.
     */
    fun getWhatsAppIntent(phone: String? = null, message: String? = null): Intent {
        val uri = if (phone != null) {
            Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message ?: "")}")
        } else {
            Uri.parse("whatsapp://send?text=${Uri.encode(message ?: "")}")
        }
        return Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.whatsapp")
        }
    }
}
