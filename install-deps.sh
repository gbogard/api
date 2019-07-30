DIR=`pwd`
DEPS_PATH="$DIR/deps"

mkdir -p $DEPS_PATH
cd $DEPS_PATH

echo "Downloading Scala in $DEPS_PATH"
wget https://downloads.lightbend.com/scala/2.12.8/scala-2.12.8.tgz
tar -zxvf scala-2.12.8.tgz

cd $DIR