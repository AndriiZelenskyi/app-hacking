(ns app-hacking.security
  (:import (java.util Base64)
           (javax.crypto SecretKeyFactory Cipher)
           (javax.crypto.spec PBEKeySpec SecretKeySpec GCMParameterSpec)
           (java.security SecureRandom)))

(def IV_SIZE 96)
(def SALT_SIZE 16)
(def algo "AES/GCM/NoPadding")
(def keyFactory (SecretKeyFactory/getInstance "PBKDF2WithHmacSHA256"))
(defn get-password [] (System/getenv "ENC_PASSWORD"))

(defn bytes->base64 [b] (.encodeToString (Base64/getEncoder) b))
(defn base64->bytes [^String s] (.decode (Base64/getDecoder) s))

(defn make-secret-key [password salt]
  (let [iterations 65536
        size 256
        keySpec (PBEKeySpec. (char-array password) salt iterations size)]
    (SecretKeySpec.
      (.getEncoded
        (.generateSecret keyFactory keySpec))
      "AES")))

(defn gen-random-nonce [size]
  (let [bytes (byte-array size)]
    (.nextBytes (SecureRandom.) bytes)
    bytes))

(defn concat-bytes [iv salt encrypted]
  (byte-array (concat (seq iv) (seq salt) (seq encrypted))))

(defn encrypt [str]
  (let [cipher (Cipher/getInstance algo)
        password (get-password)
        iv (gen-random-nonce IV_SIZE)
        salt (gen-random-nonce SALT_SIZE)
        secretKey (make-secret-key password salt)]
    (.init cipher Cipher/ENCRYPT_MODE secretKey (GCMParameterSpec. 128 iv))
    (bytes->base64 (concat-bytes iv salt (.doFinal cipher (.getBytes str))))))

(defn decrypt [str]
  (let [cipher (Cipher/getInstance algo)
        password (get-password)
        bytes (base64->bytes str)
        iv (byte-array (take IV_SIZE bytes))
        salt (byte-array (take SALT_SIZE (drop IV_SIZE bytes)))
        encrypted (byte-array (drop (+ IV_SIZE SALT_SIZE) bytes))
        secretKey (make-secret-key password salt)]
    (.init cipher Cipher/DECRYPT_MODE secretKey (GCMParameterSpec. 128 iv))
    (String. (.doFinal cipher encrypted))))
