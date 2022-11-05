# SSL Keys

This
directory
contains
the
SSL
keys
used
by
the
server.

## Generating keys

```shell
$ openssl req -x509 -newkey rsa:4096 -keyout private.pem -out public.pem -days 365 -nodes
```

## Generating keystore

```shell
$ openssl pkcs12 -export -in public.pem -inkey private.pem -out keystore.p12 -name "yomikaze"
```
