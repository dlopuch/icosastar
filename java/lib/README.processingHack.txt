This directory is a bit of a hack to make Processing work as stand-alone Java.

Long story short, on bootup (at least on OS-X), Processing sniffs out the sketch's directory by going one up from where
the core.jar file is.  This is important because things like your data/ directory for, say, loading images, is relative
to your sketch directory.  Thus, your core.jar needs to be in your root /lib/ folder.

This directory then must have a copy of core.jar, and that lib must be included in your project path.  Clever things
like symlinks don't work because Processing sniffs out the symlink'd jar's actual location.

The core.jar is .gitignore'd, so you need to initialize on fresh checkout:

-----------------
TO INITIALIZE:

  cp /Applications/Processing.app/Contents/Java/core.jar lib/