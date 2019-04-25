---
author: Andrew Tropin
title: "Guix & Nix"
subtitle: "OSes: functional approach"
title-image: "images/02/guix_and_nix.png"
date: April 25, 2019
revealjs-url: 'https://revealjs.com'
theme: white
slideNumber: true
---

# whoami
@[andrewtropin](https://github.com/abcdw)

# whereis

[https://github.com/abcdw/talks](https://github.com/abcdw/talks)


# Plan {.center}

<!-- ## Here's a slide -->

## Plan

- Current state of system distributions
- State of art
- Guix perks
- How to choose?
- How to install?


# Current state

## What SD should do?

- Software (de)installation
- Service configuration

## How we manage services?

Mutating global state using config files!

```shell
sudo vim /etc/.../postgresql.conf
sudo service postgresql restart
```

## How we install software?

Mutating global state using package managers!

```shell
apt-get install python python-pip
pip install wakatime
snap install vscode
```

## How we deal with dependencies?

- Try to make things consistent
- Static linking
- Custom prefix
- Isolate environment

Note: (virtualenv, node_modules, docker)

## Who we can trust?

- ???

## How to get reproducible environment?

- ???
- virtualization?
- shared env?

# State of the art

# History

# How package management works
- No globals state (/bin, /usr/lib)
- Pure: build runtime isolated
- Reproducible: binary cache, deduplication, trust

## Persistent store

hash(input) + package + version
``` shell
echo $PATH | sed 's/:/\n/g'
readlink ~/.guix-profile
# ...
ldd $(which zsh)
```

## User package management

Binary or source?

``` shell
guix package -s
guix package -i
guix package -u
```

## Environment

``` shell
guix environment --ad-hoc gcc@5.5.0 hello tree
# --pure
# --container
# echo /gun/store/*
```

## Challenge

``` shell
guix challenge bash
```

## Pack

``` shell
guix pack
guix pack -f docker
docker load -i
```

## Garbage collection
Remove only unused packages

## Package definition

``` scheme
(define-public hello
  (package
    (name "hello")
    (version "2.10")
    (source (origin
              (method url-fetch)
              (uri (string-append "mirror://gnu/hello/hello-" version
                                  ".tar.gz"))
              (sha256
               (base32
                "0ssi1wpaf7plaswqqjwigppsg5fyh99vdlb9kzl7c9lng89ndq1i"))))
    (build-system gnu-build-system)
    (synopsis "Hello, GNU world: An example GNU package")
    (description
     "GNU Hello prints the message \"Hello, world!\" and then exits.  It
serves as an example of standard GNU coding practices.  As such, it supports
command-line arguments, multiple languages, and so on.")
    (home-page "https://www.gnu.org/software/hello/")
    (license gpl3+)))
```

## Hackable package definition

[https://github.com/meiyopeng/guix-packages/blob/master/meiyo/packages/linux-nonfree.scm](https://github.com/meiyopeng/guix-packages/blob/master/meiyo/packages/linux-nonfree.scm)

## Import

guix import gem rails

# Inside system distribution
- Declarative

## Init?

``` shell
cat $(which shepherd)
```

## System configuration

[~/configs/guix/qemu.scm](https://github.com/abcdw/configs/blob/master/guix/qemu.scm)

``` shell
guix system reconfigure ./config.scm
```

## Services

``` scheme
(services (cons* (dhcp-client-service)
                 (service openssh-service-type
                          (openssh-configuration
                            (port-number 2222)))
                 %base-services)))
```

## VM

``` shell
guix system vm ./config.scm
```

## Explore

``` shell
guix build -S guix
```

## What is guix?

- Package definitions + bootstrap binaries
- Package manager + library
- GNU/Linux distro with declarative config


# Perks

## Package management

### Multiple versions
No DLL-hell
### Complete dependencies
No work-for-me packages from dirty envs
### Multi-user support
No trojans, but user can install packages
### Atomic upgrades and rollbacks
Switch symlinks is atomic

### Transparent source/binary deployment
--no-substitute


# How to pick a distro or package manager?
Nix vs Guix

* Package manager vs SD
* Mac vs Linux
* Haskell vs Lisp
* Diversity vs Liberty


guix pack --docker
