#!/opt/local/bin/bash
javac -d bin -sourcepath src -cp lib/diffutils-1.4.0-SNAPSHOT.jar src/edu/washington/bugisolation/BugMinimizer.java
for i in {1..65}
do
    printf "%s\n" "Lang" "$i" "true" | java -ea -cp bin:lib/diffutils-1.4.0-SNAPSHOT.jar edu.washington.bugisolation.BugMinimizer
    printf "%s\n" "Lang" "$i" "false" | java -ea -cp bin:lib/diffutils-1.4.0-SNAPSHOT.jar edu.washington.bugisolation.BugMinimizer
done
