#!/bin/bash
PREFIX="http://bits.beta.wmflabs.org/en.wikipedia.beta.wmflabs.org"
BASE_PATH="`dirname $0`/.."

wget "$PREFIX/load.php?debug=true&lang=en&modules=mobile.app.pagestyles.android&only=styles&skin=vector&version=&*" -O "$BASE_PATH/wikipedia/assets/styles.css"
wget "$PREFIX/load.php?debug=true&lang=en&modules=mobile.app.pagestyles.android&only=styles&skin=vector&version=&*" -O "$BASE_PATH/wikipedia/assets/abusefilter.css"
wget "$PREFIX/load.php?debug=true&lang=en&modules=mobile.app.preview&only=styles&skin=vector&version=&*" -O "$BASE_PATH/wikipedia/assets/preview.css"
wget "$PREFIX/load.php?debug=true&lang=en&modules=mobile.app.pagestyles.android.night&only=styles&skin=vector&version=&*" -O "$BASE_PATH/wikipedia/assets/night.css"
