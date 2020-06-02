
sources := $(wildcard */slides.md)
objects := $(sources:%/slides.md=docs/%.html)

docs/%.html : %/slides.md revealjs-header.html
	pandoc -t revealjs -s -o $@ $< \
-V controls=false -V center=true \
--template revealjs-template.html \
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
	cd docs && clojure -Sdeps '{:deps {nasus {:mvn/version "0.1.7"}}}' -m http.server 8001
