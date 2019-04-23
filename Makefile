
sources := $(wildcard */slides.md)
objects := $(sources:%/slides.md=docs/%.html)

docs/%.html : %/slides.md revealjs-header.html
	pandoc -t revealjs -s -o $@ $< \
-V controls=false -V center=true \
--standalone \
--slide-level=2 \
--no-highlight \
--include-in-header=revealjs-header.html

#-V css=custom.css \
#--highlight-style=""

# .PHONY: $(objects)

slides: $(objects)

test:
	echo $(objects)
clean:
	rm -f $(objects)

watch:
	watchexec --exts md make slides
