param(
    [Parameter(ValueFromRemainingArguments=$true)]
    $RemainingArgs
)
$MAVEN_IMAGE = $env:MAVEN_IMAGE -or 'maven:3.9.5-eclipse-temurin-17'
$args = $RemainingArgs -join ' '
docker run --rm -v "${PWD}:/usr/src/mymaven" -w "/usr/src/mymaven" $MAVEN_IMAGE mvn $args