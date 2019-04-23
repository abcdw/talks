---
author: Andrew Tropin
title: "Guix & Nix"
subtitle: "OSes: functional approach"
title-image: "https://nixos.org/logo/nixos-logo-only-hires.png"
date: April 25, 2019
revealjs-url: 'https://revealjs.com'
theme: white
slideNumber: true
---

# whoami

# Plan {.center}

<!-- ## Here's a slide -->

## Plan

- Current state of system distributions
- Problems and solutions
- How to choose?
- How to install?

# State of the art


## How we install software?

``` shell
apt-get install python python-pip
pip install wakatime
snap install vscode
```

## How we deal with dependencies?

## Free software

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
### Garbage collection
Remove only unused packages
### Transparent source/binary deployment
--no-substitute

## Operating systems
### Declarative configuration
### Reproducible deployements

# GuixSD cons and pros
## Hackable services

bootstrap -> security trust without

# Not solved


# How to pick a distro or package manager?
Nix vs Guix

* Mac vs Linux
* Haskell vs Lisp
* Diversity vs Liberty


apt, easy_install, pip for installing python applications
flatpak, snap, docker - bitcoin miners and you can't reproduce image
npm - example of naive persistent package manager
## Software environment tool
## System configuration tool
## Container provisioning

guix environment --ad-hoc python python-numpy

guix import pypi python-numpy

guix pack --docker

guix system reconfigure




declarative
