LDX #1
LDA BASE
LDB EXP
LOOP TIX EXP
JEQ FIM
MUL BASE
J LOOP
FIM END
BASE WORD 2
EXP WORD 10