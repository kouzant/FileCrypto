FileCrypto
==========
----------

Application that uses public key cryptography to encrypt contents of a
directory.

License
-------
Copyright 2015 (C)
Antonis Kouzoupis

The source code is licensed under GPLv3

Compile
-------

To compile it you will need Maven. Type **mvn package** to generate a jar file.

Run
---
**java -jar FileCrypto-1.0.jar [OPTIONS]**

usage: FileCrypto
 -d,--decrypt &nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
Decrypt operation
 -e,--encrypt &nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
Encrypt operation
 -h,--help &nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
Print help message
 -i,--inputdir &lt;arg&gt; &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
input directory
 -k,--key &lt;arg&gt; &nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
encryption/decryption key
 -o,--outputdir &lt;arg&gt; &nbsp; &nbsp;&nbsp;&nbsp; output directory
 -v,--version &nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
Print version



FileCrypto makes use of public key cryptography. In case you don't have a pair
of keys follow this micro tutorial


> - First generate a private key
> **$ openssl genrsa -out "keys/private_key.pem" 2048**
> - Generate the public key
> **$ openssl rsa -in "keys/private_key.pem" -inform pem -out
> "keys/public_key.cert" -outform der -pubout**
> - Java works with DER format, so convert the private key
> **$ openssl rsa -in "keys/private_key.pem" -inform pem -out
> "keys/private_key.key" -outform der**
