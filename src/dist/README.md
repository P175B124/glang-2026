# GLang ${project.version}

Mokomoji programavimo kalba (P175B124 „Programavimo kalbų teorija“).
Šis diegimo paketas (angl. *distribution*) – paruošta naudoti GLang interpretatoriaus versija:
šaltinio kodo ir kūrimo aplinkos nereikia.

## Reikalavimai aplinkai

- Java 21 ar naujesnė (`java -version`).

Jei `java` nėra `PATH` kintamajame, nurodykite `JAVA_HOME` – paleidimo skriptai jį naudoja.

## Įdiegimas

Išarchyvuokite paketą bet kurioje vietoje:

    unzip glang-${project.version}.zip            # arba: tar xzf glang-${project.version}.tar.gz
    cd glang-${project.version}

Pageidaujant `bin` katalogą galima pridėti prie `PATH`, tada `glang` veiks iš bet kurio katalogo.

## Turinys

    glang-${project.version}/
    ├── bin/
    │   ├── glang            paleidimo skriptas Linux / macOS
    │   └── glang.bat        paleidimo skriptas Windows
    ├── lib/
    │   └── glang.jar        GLang interpretatorius
    ├── examples/
    │   ├── *.glang          pavyzdinės programos
    │   ├── *.expected       tikėtinas kiekvienos programos rezultatas
    │   ├── run-all.sh       visų pavyzdžių paleidimas Linux / macOS
    │   └── run-all.bat      visų pavyzdžių paleidimas Windows
    └── README.md

## Programos paleidimas

    bin/glang examples/scopes.glang             # Linux / macOS
    bin\glang.bat examples\scopes.glang         # Windows

Rezultatas:

    6
    3
    1

Išėjimo kodai: `0` – sėkmė, `1` – klaida programoje arba neteisingi parametrai, `2` – failo nepavyko perskaityti.

## Klaidos

Jei programoje yra klaidų, ji nevykdoma – išvedamos visos klaidos su eilutės ir stulpelio numeriais.
Pirmiausia tikrinama, ar kintamieji paskelbti:

    bin/glang examples/errors-declarations.glang

    Semantic errors:
    line 3:6 Variable 'y' is not declared.
    line 4:0 Variable 'z' is not declared.
    line 8:6 Variable 'inner' is not declared.
    line 9:4 Variable 'x' is already declared.

Tipai tikrinami tik tada, kai kintamųjų klaidų nėra:

    bin/glang examples/errors-types.glang

    Semantic errors:
    line 3:0 Cannot assign bool to variable 'x' of type int.
    line 4:4 Condition must be bool, got int.
    line 5:12 Operator '+' expects int operands, got int and bool.
    line 7:12 Operator '<' expects int operands, got bool and int.

## Visų pavyzdžių paleidimas

    examples/run-all.sh                 # Linux / macOS
    examples\run-all.bat                # Windows

Kiekviena programa paleidžiama, jos rezultatas palyginamas su `<vardas>.expected` failu:

    PASS  arithmetic.glang
    PASS  errors-declarations.glang
    PASS  errors-types.glang
    PASS  if.glang
    PASS  print.glang
    PASS  scopes.glang
    PASS  variables.glang

    7 passed, 0 failed
