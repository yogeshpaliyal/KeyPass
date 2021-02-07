package com.yogeshpaliyal.keypass.db_helper

import com.yogeshpaliyal.universal_adapter.utils.LogHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.InvalidKeyException
import java.security.Key
import java.security.NoSuchAlgorithmException
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 07-02-2021 18:50
*/
object EncryptionHelper {
    private const val ALGORITHM = "AES"
    private const val TRANSFORMATION = "AES"
    // private const val TRANSFORMATION = "DES/CBC/PKCS5Padding"

    @Throws(CryptoException::class)
    fun encrypt(key: String?, inputFile: File?, outputFile: File?) {
        LogHelper.logD("FindingBug", "Key => ${key} \n inputFile => ${inputFile?.path} \n Output File => ${outputFile?.path}")

        doCryptoEncrypt(Cipher.ENCRYPT_MODE, key!!, inputFile!!, outputFile!!)
    }

    @Throws(CryptoException::class)
    fun decrypt(
        key: String?,
        inputFile: File?,
        outputFile: File?
    ) {
        doCryptoDecrypt(Cipher.DECRYPT_MODE, key!!, inputFile!!, outputFile!!)
    }


    /*@Throws(CryptoException::class)
    private fun doCrypto(
        cipherMode: Int, key: String, inputFile: File,
        outputFile: File
    ) {
        try {
            val secretKey: Key =
                SecretKeySpec(key.toByteArray(), ALGORITHM)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(cipherMode, secretKey)
            val inputStream = FileInputStream(inputFile)

            val inputBytes = inputFile.readBytes()

            val outputBytes = cipher.doFinal(inputBytes)
            val outputStream = FileOutputStream(outputFile)
            outputStream.write(outputBytes)
            inputStream.close()
            outputStream.close()
        } catch (ex: NoSuchPaddingException) {
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: NoSuchAlgorithmException) {
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: InvalidKeyException) {
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: BadPaddingException) {
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: IllegalBlockSizeException) {
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: IOException) {
            throw CryptoException("Error encrypting/decrypting file", ex)
        }
    }*/

    @Throws(CryptoException::class)
    private fun doCrypto(
        cipherMode: Int, key: String, inputFile: File,
        outputFile: File
    ) {
        try {
            val secretKey: Key =
                SecretKeySpec(key.toByteArray(), ALGORITHM)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(cipherMode, secretKey)
            val inputStream = FileInputStream(inputFile)
            val outputStream = FileOutputStream(outputFile)

            val cis = CipherInputStream(inputStream, cipher)
            var numberOfBytedRead: Int = 0
            val buffer = ByteArray(4096)

            while (numberOfBytedRead >= 0) {
                outputStream.write(buffer, 0, numberOfBytedRead);
                numberOfBytedRead = cis.read(buffer)
            }

            inputStream.close()
            outputStream.close()
        } catch (ex: NoSuchPaddingException) {
            //Log.d("TestingEnc","NoSuchPaddingException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: NoSuchAlgorithmException) {
            //Log.d("TestingEnc","NoSuchAlgorithmException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: InvalidKeyException) {
            // Log.d("TestingEnc","InvalidKeyException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: BadPaddingException) {
            // Log.d("TestingEnc","BadPaddingException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: IllegalBlockSizeException) {
            // Log.d("TestingEnc","IllegalBlockSizeException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: IOException) {
            // Log.d("TestingEnc","IOException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        }
    }


    @Throws(CryptoException::class)
    private fun doCryptoEncrypt(
        cipherMode: Int, key: String, inputFile: File,
        outputFile: File
    ) {
        try {
            val secretKey: Key =
                SecretKeySpec(key.toByteArray(), ALGORITHM)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(cipherMode, secretKey)
            // val inputStream = FileInputStream(inputFile)
            // val outputStream = FileOutputStream(outputFile)

            FileInputStream(inputFile).use {
                val inputStream = it
                FileOutputStream(outputFile).use {
                    val outputStream = it
                    CipherOutputStream(outputStream, cipher).use {
                        inputStream.copyTo(it, 4096)
                    }
                }
            }

            /* val cis = CipherOutputStream(outputStream, cipher)
             var numberOfBytedRead: Int = 0
             val buffer = ByteArray(4096)

             while (numberOfBytedRead >= 0) {
                 cis.write(buffer, 0, numberOfBytedRead);
                 numberOfBytedRead = inputStream.read(buffer)
             }
             cis.flush()
             cis.close()
             inputStream.close()
             outputStream.close()*/
        } catch (ex: NoSuchPaddingException) {
            // Log.d("TestingEnc","NoSuchPaddingException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: NoSuchAlgorithmException) {
            // Log.d("TestingEnc","NoSuchAlgorithmException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: InvalidKeyException) {
            //Log.d("TestingEnc","InvalidKeyException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: BadPaddingException) {
            // Log.d("TestingEnc","BadPaddingException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: IllegalBlockSizeException) {
            // Log.d("TestingEnc","IllegalBlockSizeException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: IOException) {
            // Log.d("TestingEnc","IOException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        }
    }

    @Throws(CryptoException::class)
    private fun doCryptoDecrypt(
        cipherMode: Int, key: String, inputFile: File,
        outputFile: File
    ) {
        try {
            val secretKey: Key =
                SecretKeySpec(key.toByteArray(), ALGORITHM)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(cipherMode, secretKey)


            FileInputStream(inputFile).use {
                val inputStream = it
                FileOutputStream(outputFile).use {
                    val outputStream = it
                    CipherInputStream(inputStream, cipher).use {
                        it.copyTo(outputStream, 4096)
                    }
                }
            }


            /*var numberOfBytedRead: Int = 0
            val buffer = ByteArray(4096)*/


            /*while (numberOfBytedRead >= 0) {
                outputStream.write(buffer, 0, numberOfBytedRead);
                numberOfBytedRead = cin.read(buffer)
            }*/

        } catch (ex: NoSuchPaddingException) {
            //Log.d("TestingEnc","NoSuchPaddingException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: NoSuchAlgorithmException) {
            //Log.d("TestingEnc","NoSuchAlgorithmException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: InvalidKeyException) {
            //Log.d("TestingEnc","InvalidKeyException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: BadPaddingException) {
            //Log.d("TestingEnc","BadPaddingException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: IllegalBlockSizeException) {
            //Log.d("TestingEnc","IllegalBlockSizeException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        } catch (ex: IOException) {
            // Log.d("TestingEnc","IOException")
            throw CryptoException("Error encrypting/decrypting file", ex)
        }
    }

}