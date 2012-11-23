#!/bin/bash

FILE="user.properties"
DIR="src"

usage() {
	echo -e "Usage: $0 <username> <plaintext-password>\n" 1>&2
	echo -en "Adds a user and the md5 hash of its password" 1>&2
	echo -e "to the $FILE file" 1>&2
}

if [ $# -ne 2 ]; then
	usage
	exit 1
fi

if ! which md5sum 1>/dev/null 2>&1; then
	echo "You don't have md5sum installed on your system!" 1>&2
	exit 1
fi

echo "# password for user $1 is the MD5 hash of \"$2\"" >> $DIR/$FILE
echo "$1 = $(echo -n $2 | md5sum | grep -o '^\S\+')" >> $DIR/$FILE
