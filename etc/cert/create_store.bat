keytool -genkeypair -keyalg rsa -keystore hunspellmerge.jks -validity 1095 -alias hunspellmerge -storepass hunspellmerge -keypass hunspellmerge
keytool -selfcert -alias hunspellmerge -keystore hunspellmerge.jks
keytool -list -keystore hunspellmerge.jks