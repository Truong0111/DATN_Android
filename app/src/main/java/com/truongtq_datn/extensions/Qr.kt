package com.truongtq_datn.extensions

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class Qr {
    companion object {
        fun generateQRCode(text: String): Bitmap? {
            val writer = QRCodeWriter()
            return try {
                val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bitmap.setPixel(
                            x,
                            y,
                            if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                        )
                    }
                }
                bitmap
            } catch (e: WriterException) {
                e.printStackTrace()
                null
            }
        }
    }
}