keytool -importcert -v -trustcacerts -file "terndoi.cer" -alias TernDOICer -keystore "ternkeystore.bks" -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "bcprov-jdk16-1.46.jar" -storetype BKS -storepass mysecret