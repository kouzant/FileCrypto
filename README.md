FileCrypto
==========
----------

Application that uses public key cryptography to encrypt contents of a
directory.

~~Note: Currently FileCrypto uses only RSA encryption, so each file size is limited by the key size. That is for key 
2048 bits, is limited to 245 byte :P Future releases (hopefully) will use both AES and RSA.~~

Version 2.0 supports RSA and AES-256 ciphers.

Running FileCrypto with multiple threads might harm the performance depending on the size of the files you want to
encrypt/decrypt. For small files (e.g. 50Mb) context switching and scheduling is a great overhead compared to the time
needed to encrypt the file, so consider using only one thread with the **-t, --threshold** option. In case your files
are of size greater than 300Mb then switch to multithreading. In multithreading most probably you will also have to
tune Java heap size respectively to file size (-Xms<Size> -Xmx<Size>).

License
-------
Copyright 2015 (C)
Antonis Kouzoupis

The source code is licensed under GPLv3

Compile
-------

To compile it you will need Maven. Type **mvn package -DskipTests** to generate
a jar file without running the unit tests.

Run
---

**The keys in directory keys_test/ are only for TESTING purposes. You MUST create a new pair!**

**java -jar FileCrypto-XX.jar [OPTIONS]**

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
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
encryption/decryption key

 -o,--outputdir &lt;arg&gt; &nbsp; &nbsp;&nbsp;&nbsp; output directory
 
 -t,--threshold &lt;arg&gt; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Threshold to dispatch files to multiple threads.
 
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;DEFAULT 300 files

 -v,--version &nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
Print version



FileCrypto makes use of public key cryptography. In case you don't have a pair
of keys follow this micro tutorial


> - First generate a private key
>
> **$ openssl genrsa -out "keys/private_key.pem" 2048**
> - Generate the public key
>
> **$ openssl rsa -in "keys/private_key.pem" -inform pem -out "keys/public_key.cert" -outform der -pubout**
> - Java works with DER format, so convert the private key
>
> **$ openssl rsa -in "keys/private_key.pem" -inform pem -out "keys/private_key.key" -outform der**
