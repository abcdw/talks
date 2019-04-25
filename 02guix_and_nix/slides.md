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

## `whereis`

[github.com/abcdw/talks](https://github.com/abcdw/talks)


## `whoami && who`

- github: [abcdw](https://github.com/abcdw)
- tg: [andrewtropin](https://t.me/andrewtropin)


# Plan {.center}

- Current state of system distributions
- State of art
- Guix perks
- How to choose?
- How to install?


# Current [state]()


## What SD should do?

- Software (de)installation
- Service configuration

## How we manage services?

Mutating global state using config files!

```shell
sudo vim /etc/.../postgresql.conf
sudo service postgresql restart
```

Which can conflict with maintainer version.

## How we install software?

Mutating global state using package managers!

```shell
apt-get install python python-pip
pip install wakatime
snap install vscode
curl https://nixos.org/nix/install | sh
./configure && make && make install
```

Which can't be undone.

## Who we can trust?

???

## How we deal with

### dependencies and conflicts?

- Try to make things consistent
- Static linking
- Custom prefix
- Give up
- Isolate environment

Note: (virtualenv, node_modules, docker)

## How to get reproducible environment?

- ???
- virtualization?
- shared env?

# ~~State~~ of the art

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
# echo /gnu/store/*
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

``` scheme
guix gc
```

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


## Init?

``` shell
cat $(which shepherd)
```

## System configuration

[~/configs/guix/qemu.scm](https://github.com/abcdw/configs/blob/master/guix/qemu.scm)

``` shell
guix system reconfigure ./config.scm
guix system search ssh
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

# Recap

## What is guix?

- Package definitions + bootstrap binaries
- Package manager + library
- GNU/Linux distro with declarative config

on top of minimalistic language

## Multiple versions
No DLL-hell

## Complete dependencies
No work-for-me packages from dirty envs

## Multi-user support
No trojans, but user can install packages

## Atomic upgrades and rollbacks
Switch symlinks is atomic

## Transparent source/binary deployment
`--no-substitute`

## Pure and Declarative

config -> system

## Hackable, introspectable and uniform

Learn the scheme - rule the system

# Problems

# How to install?

## Patition hard drive

## Create system configuration

``` scheme
(operating-system
 (host-name "functional-machine")
 (timezone "Europe/Moscow")
 (locale "ru_RU.utf8")
 (bootloader (grub-configuration (device "/dev/sda")))
 (file-systems (cons (file-system
                      (device "my-root")
                      (title ’label)
                      (mount-point "/")
                      (type "ext4"))
                     %base-file-systems))
 (users (cons (user-account
               (name "bob")
               (group "users")
               (home-directory "/home/bob"))
              %base-user-accounts))
 (services (cons* (dhcp-client-service)
                  (service openssh-service-type)
                  %base-services)))
```

# How to configure?

## Define more services

``` scheme
(service openssh-service-type
         (openssh-configuration
          (x11-forwarding? #t)
          (permit-root-login ’without-password)))
```

## Update service list

``` scheme
(operating-system
 ;; ...
 (services (remove (lambda (service)
                           (eq? ntp-service-type
                                (service-kind service)))
                   %desktop-services)))
```

## Wrap service into container
``` scheme
(with-imported-modules
 ’((gnu build linux-container))
  (shepherd-service
   (provision ’(bitlbee))
   (requirement ’(loopback))
   (start #~(make-forkexec-constructor/container
	     (list #$(file-append bitlbee "/sbin/bitlbee")
		   ...)))
   (stop #~(make-kill-destructor))))
```

# How to pick right1?

Nix vs Guix

* Package manager vs SD
* Mac vs Linux
* Haskell vs Lisp
* Diversity vs Liberty

# Help

- #nixos, #guix on freenode
- [https://www.gnu.org/software/guix/help/](https://www.gnu.org/software/guix/help/)
- [https://nixos.org/nixos/support.html](https://nixos.org/nixos/support.html)
- [Referenc card](https://www.gnu.org/software/guix/guix-refcard.pdf)

# contact me

- github: [abcdw](https://github.com/abcdw)
- tga: [andrewtropin](https://t.me/andrewtropin)

[github.com/abcdw/talks](https://github.com/abcdw/talks)
