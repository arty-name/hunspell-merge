var err = initInstall("[%Description%]", "[%FileName%]@dictionaries.addons.mozilla.org", "1.0");
if (err != SUCCESS)
    cancelInstall();

var fProgram = getFolder("Program");
err = addDirectory("", "[%FileName%]@dictionaries.addons.mozilla.org", "dictionaries", fProgram, "dictionaries", true);
if (err != SUCCESS)
    cancelInstall();

performInstall();