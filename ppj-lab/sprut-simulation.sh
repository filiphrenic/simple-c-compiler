# compile
javac GLA.java analizator/LA.java

# generate objects.ost
java GLA < test.lan

# run lexical analysis
java analizator.LA < test.in > test.out

# clean up
find . -name "*.class" | xargs rm -rf
rm -rf analizator/objects.ost

# show results
less test.out