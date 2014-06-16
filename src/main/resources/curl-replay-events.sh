#!/bin/bash

# get events.in.<xxx>log of the day(s) and prepare file,
# i.e. remove timestamp etc:
# for event in `cat events.in.<xxx>.log`; do $event | awk '{print $3}' >> ready-for-replay-file; done
#
# Convert to json:
# for event in `cat ready-for-replay-file`; do echo -e `echo $event | base64 --decode` >> events-as.json; done
#
# consider splitting the file and run multiple scripts
# rewrite as:
# while read line
# do
#
# done < file.txt

URL=http://events.workshare.com/track
FILE=events-stripped-uniq-05
EXPECTED_STATUS_CODE=1200
echo "`wc -l $FILE`"

COUNTER=1
for event in `cat $FILE`;
        do
                if [ $((COUNTER % 100)) == 0 ]
                        then echo $COUNTER 'events processed'
                fi

                #echo -e "\ncurl -s -w "%{http_code}" -XGET $URL?data="$event
                status=$(curl -s -w "%{http_code}" -XGET $URL?data=$event)
                echo $status
                if [ $status -eq $EXPECTED_STATUS_CODE ]
                  then echo $event >> replay-events-done
                else
                  echo $event >> replay-events-problem
                fi
                sleep .05
                COUNTER=$((COUNTER+1));
done