#!/bin/sh

case $1 in
    all) mvn install;;
    app) cd EightySix-app; mvn install android:deploy android:run; cd ..;;
esac

exit 0;