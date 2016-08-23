#!/bin/bash
echo "Converting the CER to a PEM"
openssl x509 -inform DER -outform PEM -in terndoi.cer -out terndoi.pem
echo "DONE - new file is terndoi.pem"
echo " "
echo "You can look at what's in your new PEM file with:"
echo " openssl x509 -noout -text -in terndoi.pem"
echo "Alternatively, you can look at the old CER file with:"
echo " openssl x509 -noout -text -inform DER -in terndoi.cer"
# Thanks http://unix.stackexchange.com/a/131338/68885
