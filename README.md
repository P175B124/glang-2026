# GLang 2026

Mokomoji programavimo kalba, kuriama P175B124 „Programavimo kalbų teorija“ paskaitose:

- **T04** – gramatika, lankytojas (visitor), interpretatorius;
- **T08** – semantinė analizė ir kompiliavimas.

Kalba sąmoningai nebaigta: daug dalykų dar neveikia. Jie surašyti kaip
[žinomos problemos](#žinomos-problemos) ir užfiksuoti testais.

## Įrankiai

- Java 21
- Maven 3.9+
- ANTLR 4.13.2
- IntelliJ IDEA su ANTLR v4 įskiepiu

## Paleidimas

    mvn clean package
    java -jar target/glang.jar samples/if.glang

IntelliJ: *All Tests*, *generate-visitor*, *glang-2026 [clean,package]*,
*Run current .glang file* (paleidžia šiuo metu atidarytą `.glang` failą).

## Diegimo paketas

`mvn clean package` iš šaltinio kodo sukuria ir diegimo paketą (angl. *distribution*):

    target/glang-1.0.0.zip
    target/glang-1.0.0.tar.gz

    glang-1.0.0/
    ├── bin/glang, bin/glang.bat     paleidimo skriptai
    ├── lib/glang.jar                visos priklausomybės viename jar
    ├── examples/                    samples/*.glang + *.expected, run-all.sh / run-all.bat
    └── README.md                    naudotojo instrukcija (src/dist/README.md)

Paketo turinį aprašo [src/assembly/dist.xml](src/assembly/dist.xml), skriptai – [src/dist](src/dist).
Patikrinimas švarioje vietoje:

    cd /tmp && unzip ~/glang-2026/target/glang-1.0.0.zip
    glang-1.0.0/examples/run-all.sh

„GitHub Release“ sukuriamas automatiškai ([release.yml](.github/workflows/release.yml)),
kai į GitHub įkeliama versijos žymė:

    git tag v1.0.0
    git push origin v1.0.0

## Žingsniai

Kiekvienas žingsnis – du commit'ai: *Grammar and tests* (nauji testai raudoni)
ir *Implementation* (visi testai žali). Žyma rodo į veikiančią būseną:

    git checkout step-3

| Žyma     | Kas pridėta                                                      |
|----------|------------------------------------------------------------------|
| `step-0` | `print(42);` – įrankių grandinės patikrinimas                    |
| `step-1` | Viena operacija: `print(2 + 3);`                                 |
| `step-2` | Kelios operacijos: `print(1 + 2 + 3);`, alternatyvų žymės (`#addExpr`) |
| `step-3` | Kintamieji: `let`, priskyrimas, simbolių lentelė                 |
| `step-4` | Loginės reikšmės `true`/`false`, palyginimai `==` `!=` `<` `>`   |
| `step-5` | `if` / `else` (vienas sakinys, delegavimas `IfVisitor`)          |
| `step-6` | Blokas `{ }` – privalomas `if` šakose (dar be galiojimo sričių) |
| `step-7` | Semantinė analizė, 1 etapas `DeclarationChecker`: paskelbimas prieš naudojimą, tik vieną kartą |
| `step-8` | Galiojimo sritys: simbolių lentelė – galiojimo sričių dėklas (`SymbolTable`) |
| `step-9` | Semantinė analizė, 2 etapas `TypeChecker`: lankytojas grąžina tipą (`int`, `bool`), ne reikšmę |
| `step-100a` | Diegimo paketas: `bin/`, `lib/`, `examples/`, `README.md` viename archyve (100 – iki jo bus dar daug žingsnių) |

## Žinomos problemos

Visos jos yra [KnownProblemsTest](src/test/java/edu/ktu/glang/core/KnownProblemsTest.java)
kaip `@Disabled` testai. Nuimkite `@Disabled` ir pasiekite, kad testas praeitų.

- daugyba, dalyba ir operacijų prioritetas (`1 + 2 * 3` turi būti `7`);
- neigiami skaičiai, skliaustai;
- loginės operacijos `&&`, `||`, `!`, palyginimai `<=`, `>=`;
- eilutės (string) tipas;
- blokai `{ }`, `while` ciklas;
- nedeklaruoti ir pakartotinai deklaruoti kintamieji, tipų klaidos
  (`1 + true`, `if (1)`) – sprendžiama semantine analize (**T08**).
