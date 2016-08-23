#!/bin/bash
cd `dirname $0`
keytool -list \
  -keystore ../src/main/resources/ternkeystore.bks \
  -storetype BKS \
  -providerpath bcprov-jdk16-1.46.jar \
  -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
  -storepass mysecret
