#!/bin/bash

# Track some info
#totalFiles = 0
#dlFiles = 0

# dl the library and all its versions renaming the lib as needed
function dl {
  for ver in $1 
  do
    totalFiles=$((totalFiles+1)) 
    ver="${ver//[[:blank:]]/}"
    if [[ -z "$4" ]]
    then 
      fn=$(basename $3)
      fn="${fn%.*}-$ver.js"
    else
      fn=$4
    fi
    curlThis "$fn" "$2/$ver/$3"
  done
}

function curlThis {
  fn=$1
  url=$2
  if [ -f "$fn" ] 
  then
    echo "$fn already downloaded."
  else
    echo "Downloading $fn..."
    curl -# -o $fn $url
#      echo curl -# -o $fn "$2/$ver/$3"
    if [[ $(head -n 1 "$fn") = "Not Found" ]]
    then
      echo "ERROR, $fn not found."
      rm $fn
    else 
      dlFiles=$((dlFiles+1)) 
    fi
  fi
} 

# override the delimiter
IFS=","

#
# dl all the js libs
#
pkgDled="$pkgDled angularjs"
VERS="1.2.23, 1.2.22, 1.2.21, 1.2.20, 1.2.19, 1.2.18, 1.2.17, 1.2.16, 1.2.15, 1.2.14, 1.2.13, 1.2.12, 1.2.11, 1.2.10, 1.2.9, 1.2.8, 1.2.7, 1.2.6, 1.2.5, 1.2.4, 1.2.3, 1.2.2, 1.2.1, 1.2.0, 1.0.8, 1.0.7, 1.0.6, 1.0.5, 1.0.4, 1.0.3, 1.0.2, 1.0.1"
dl "$VERS" "http://ajax.googleapis.com/ajax/libs/angularjs" "angular.min.js"

pkgDled="$pkgDled dojojs"
VERS="1.10.0, 1.9.3, 1.9.2, 1.9.1, 1.9.0, 1.8.6, 1.8.5, 1.8.4, 1.8.3, 1.8.2, 1.8.1, 1.8.0, 1.7.5, 1.7.4, 1.7.3, 1.7.2, 1.7.1, 1.7.0, 1.6.2, 1.6.1, 1.6.0, 1.5.3, 1.5.2, 1.5.1, 1.5.0, 1.4.5, 1.4.4, 1.4.3, 1.4.1, 1.4.0, 1.3.2, 1.3.1, 1.3.0, 1.2.3, 1.2.0, 1.1.1"
dl "$VERS" "http://ajax.googleapis.com/ajax/libs/dojo" "dojo/dojo.js"

pkgDled="$pkgDled extjs"
VERS="3.1.0, 3.0.0"
dl "$VERS" "http://ajax.googleapis.com/ajax/libs/ext-core" "ext-core.js"

pkgDled="$pkgDled jquery"
VERS="2.1.1, 2.1.0, 2.0.3, 2.0.2, 2.0.1, 2.0.0, 1.11.1, 1.11.0, 1.10.2, 1.10.1, 1.10.0, 1.9.1, 1.9.0, 1.8.3, 1.8.2, 1.8.1, 1.8.0, 1.7.2, 1.7.1, 1.7.0, 1.6.4, 1.6.3, 1.6.2, 1.6.1, 1.6.0, 1.5.2, 1.5.1, 1.5.0, 1.4.4, 1.4.3, 1.4.2, 1.4.1, 1.4.0, 1.3.2, 1.3.1, 1.3.0, 1.2.6, 1.2.3"
dl "$VERS" "http://ajax.googleapis.com/ajax/libs/jquery" "jquery.min.js"

pkgDled="$pkgDled jquery.mobile"
VERS="1.4.3, 1.4.2, 1.4.1, 1.4.0"
dl "$VERS" "http://ajax.googleapis.com/ajax/libs/jquerymobile" "jquery.mobile.min.js"

pkgDled="$pkgDled mootools"
VERS="1.5.0, 1.4.5, 1.4.4, 1.4.3, 1.4.2, 1.4.1, 1.4.0, 1.3.2, 1.3.1, 1.3.0, 1.2.5, 1.2.4, 1.2.3, 1.2.2, 1.2.1, 1.1.2, 1.1.1"
dl "$VERS" "http://ajax.googleapis.com/ajax/libs/mootools" "mootools-yui-compressed.js"

pkgDled="$pkgDled prototypejs"
VERS="1.7.2.0, 1.7.1.0, 1.7.0.0, 1.6.1.0, 1.6.0.3, 1.6.0.2"
dl "$VERS" "http://ajax.googleapis.com/ajax/libs/prototype" "prototype.js"

pkgDled="$pkgDled scriptaculous"
VERS="1.9.0, 1.8.3, 1.8.2, 1.8.1"
dl "$VERS" "http://ajax.googleapis.com/ajax/libs/scriptaculous" "scriptaculous.js"

pkgDled="$pkgDled swfobject"
VERS="2.2, 2.1"
dl "$VERS" "http://ajax.googleapis.com/ajax/libs/swfobject" "swfobject.js"

pkgDled="$pkgDled threejs"
VERS="r67"
dl "$VERS" "http://ajax.googleapis.com/ajax/libs/threejs" "three.min.js"

pkgDled="$pkgDled webfont"
VERS="1.5.3, 1.5.2, 1.5.0, 1.4.10, 1.4.8, 1.4.7, 1.4.6, 1.4.2, 1.3.0, 1.1.2, 1.1.1, 1.1.0, 1.0.31, 1.0.30, 1.0.29, 1.0.28, 1.0.27, 1.0.26, 1.0.25, 1.0.24, 1.0.23, 1.0.22, 1.0.21, 1.0.19, 1.0.18, 1.0.17, 1.0.16, 1.0.15, 1.0.14, 1.0.13, 1.0.12, 1.0.11, 1.0.10, 1.0.9, 1.0.6, 1.0.5, 1.0.4, 1.0.3, 1.0.2, 1.0.1, 1.0.0"
dl "$VERS" "http://ajax.googleapis.com/ajax/libs/webfont" "webfont.js"

pkgDled="$pkgDled yui"
VERS="3.17.2, 3.17.1, 3.17.0, 3.16.0, 3.15.0, 3.14.1, 3.14.0, 3.13.0, 3.12.0, 3.11.0, 3.10.3, 3.10.2, 3.10.1, 3.10.0, 3.9.1, 3.9.0, 3.8.1, 3.8.0, 3.7.3, 3.7.2, 3.7.1, 3.7.0, 3.6.0, 3.5.1, 3.5.0, 3.4.1, 3.4.0, 3.3.0, 3.2.0, 3.1.2, 3.1.1, 3.1.0, 3.0.0"
dl "$VERS" "http://yui.yahooapis.com" "build/yui/yui-min.js"
dl "$VERS" "http://yui.yahooapis.com" "build/yui/yui.js"

pkgDled="$pkgDled backbonejs"
VERS="1.1.2, 1.1.1, 1.1.0, 1.0.0, 0.9.10, 0.9.9, 0.9.2, 0.9.1, 0.9.0, 0.5.3, 0.5.2, 0.5.1, 0.5.0, 0.3.3, 0.3.2, 0.3.1, 0.3.0, 0.2.0, 0.1.2, 0.1.1, 0.1.0"
dl "$VERS" "https://raw.githubusercontent.com/jashkenas/backbone" "backbone.js"
VERS="1.1.2, 1.1.1, 1.1.0, 1.0.0, 0.9.10, 0.9.9, 0.9.2, 0.9.1, 0.9.0"
dl "$VERS" "https://raw.githubusercontent.com/jashkenas/backbone" "backbone-min.js"

pkgDled="$pkgDled spinejs"
VERS="v.1.3.2, v1.3.1, v1.3.0, v1.2.2, v1.2.1, v1.2.0, v1.1.0, v1.0.9, v1.0.8, v1.0.7, v1.0.6, v1.0.5, v1.0.3, v1.0.2, v1.0.1, v1.0.0, v0.0.9, 1.0.9"
dl "$VERS" "https://raw.githubusercontent.com/spine/spine" "lib/spine.js"

pkgDled="$pkgDled agilityjs"
VERS="0.1.3, 0.1.2, 0.1.1, 0.1.0"
dl "$VERS" "https://raw.githubusercontent.com/arturadib/agility" "agility.js"
VERS="0.1.3, 0.1.2, 0.1.1"
dl "$VERS" "https://raw.githubusercontent.com/arturadib/agility" "docs/agility.min.js"

pkgDled="$pkgDled sammyjs"
VERS="v0.7.6, v0.7.5, v0.7.4, v0.7.3, v0.7.2, v0.7.1, v0.7.0, v0.6.3, v0.6.2, v0.6.1, v0.6.0, v0.5.4, v0.5.3, v0.5.2, v0.5.1, v0.5.0, v0.4.1, v0.4.0, v0.3.0, v0.2.1, v0.2.0, v0.1.4, v0.1.3, 0.5.0"
dl "$VERS" "https://raw.githubusercontent.com/quirkey/sammy" "lib/sammy.js"
VERS="v0.7.6, v0.7.5, v0.7.4, v0.7.3, v0.7.2, v0.7.1, v0.7.0, v0.6.3, v0.6.2, v0.6.1, v0.6.0, v0.5.4"
dl "$VERS" "https://raw.githubusercontent.com/quirkey/sammy" "lib/min/sammy-latest.min.js"
VERS="v0.5.3, v0.5.2, v0.5.1, v0.5.0, v0.4.1, v0.4.0, v0.3.0"
dl "$VERS" "https://raw.githubusercontent.com/quirkey/sammy" "lib/min/sammy-lastest.min.js"
VERS="v0.2.1, v0.2.0, v0.1.4, v0.1.3"
dl "$VERS" "https://raw.githubusercontent.com/quirkey/sammy" "lib/sammy.min.js"

pkgDled="$pkgDled snackjs"
VERS="v1.2.3, v1.2.2, v1.2.1, v1.2.0, v1.1.0, v1.0.0"
dl "$VERS" "https://raw.githubusercontent.com/rpflorence/snack" "builds/snack.js"
dl "$VERS" "https://raw.githubusercontent.com/rpflorence/snack" "builds/snack-slick.js"
dl "$VERS" "https://raw.githubusercontent.com/rpflorence/snack" "builds/snack-slick-min.js"
dl "$VERS" "https://raw.githubusercontent.com/rpflorence/snack" "builds/snack-sizzle.js"
dl "$VERS" "https://raw.githubusercontent.com/rpflorence/snack" "builds/snack-sizzle-min.js"
dl "$VERS" "https://raw.githubusercontent.com/rpflorence/snack" "builds/snack-qwery.js"
dl "$VERS" "https://raw.githubusercontent.com/rpflorence/snack" "builds/snack-qwery-min.js"
dl "$VERS" "https://raw.githubusercontent.com/rpflorence/snack" "builds/snack-min.js"

pkgDled="$pkgDled emberjs"
VERS="v1.7.0"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v1.6.1"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v1.6.0"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v1.5.1"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v1.5.0"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v1.4.0"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v1.3.2"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v1.3.1"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v1.3.0"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v1.2.0"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v1.1.2"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v1.1.1"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v1.0.0"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v0.9.8.1"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.min.js" "ember-${VERS:1}.min.js"
VERS="v0.9.8"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.min.js" "ember-${VERS:1}.min.js"
VERS="v0.9.7.1"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.min.js" "ember-${VERS:1}.min.js"
VERS="v0.9.7"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.min.js" "ember-${VERS:1}.min.js"
VERS="v0.9.6"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.min.js" "ember-${VERS:1}.min.js"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v0.9.5"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.min.js" "ember-${VERS:1}.min.js"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
VERS="v0.9.4"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.min.js" "ember-${VERS:1}.min.js"
VERS="v0.9.3"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.min.js" "ember-${VERS:1}.min.js"
VERS="v0.9.2"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.min.js" "ember-${VERS:1}.min.js"
VERS="v0.9.1"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.min.js" "ember-${VERS:1}.min.js"
VERS="v0.9"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.js" "ember-${VERS:1}.js"
dl "$VERS" "https://raw.githubusercontent.com/emberjs/starter-kit" "js/libs/ember-${VERS:1}.min.js" "ember-${VERS:1}.min.js"

pkgDled="$pkgDled stapesjs"
VERS="v0.8.1, v0.8.0, v0.7.1, v0.7.0, v0.6, v0.5.1, v0.5, v0.4, v0.3, v0.2.1, v0.2"
dl "$VERS" "https://raw.githubusercontent.com/hay/stapes" "stapes.js"
VERS="v0.8.1, v0.8.0, v0.7.1, v0.7.0, v0.6, v0.5.1, v0.5, v0.4, v0.3, v0.2.1"
dl "$VERS" "https://raw.githubusercontent.com/hay/stapes" "stapes.min.js"

pkgDled="$pkgDled qunit"
VERS="v1.14.0, v1.13.0, v1.12.0, v1.11.0, v1.10.0, v1.9.0, v1.8.0, v1.7.0, v1.6.0, v1.5.0, v1.4.0, v1.3.0, v1.2.0, v1.1.0, v1.0.0, 1.15.0, 1.14.0, 1.13.0, 1.1.0"
dl "$VERS" "https://raw.githubusercontent.com/jquery/qunit" "qunit/qunit.js"

pkgDled="$pkgDled serenadejs"
curlThis serenade.0.5.0.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.5.0.js
curlThis serenade.0.4.2.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.4.2.js
curlThis serenade.0.4.1.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.4.1.js
curlThis serenade.0.4.0.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.4.0.js
curlThis serenade.0.3.0.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.3.0.js
curlThis serenade.0.2.1.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.2.1.js
curlThis serenade.0.2.0.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.2.0.js
curlThis serenade.0.1.3.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.1.3.js
curlThis serenade.0.1.2.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.1.2.js
curlThis serenade.0.1.1.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.1.1.js
curlThis serenade.0.1.0.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.1.0.js
curlThis serenade.0.5.0.min.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.5.0.min.js
curlThis serenade.0.4.2.min.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.4.2.min.js
curlThis serenade.0.4.1.min.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.4.1.min.js
curlThis serenade.0.4.0.min.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.4.0.min.js
curlThis serenade.0.3.0.min.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.3.0.min.js
curlThis serenade.0.2.1.min.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.2.1.min.js
curlThis serenade.0.2.0.min.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.2.0.min.js
curlThis serenade.0.1.3.min.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.1.3.min.js
curlThis serenade.0.1.2.min.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.1.2.min.js
curlThis serenade.0.1.1.min.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.1.1.min.js
curlThis serenade.0.1.0.min.js http://serenade-downloads.s3-website-us-east-1.amazonaws.com/serenade.0.1.0.min.js

pkgDled="$pkgDled feathersjs"
VERS="0.1.0, 0.0.1"
dl "$VERS" "https://raw.githubusercontent.com/feathersjs/canjs-feathers" "lib/feathers.js"

pkgDled="$pkgDled amplifyjs"
VERS="1.1.2, 1.1.1"
dl "$VERS" "https://raw.githubusercontent.com/appendto/amplify" "lib/amplify.core.js"
dl "$VERS" "https://raw.githubusercontent.com/appendto/amplify" "lib/amplify.core.min.js"
dl "$VERS" "https://raw.githubusercontent.com/appendto/amplify" "lib/amplify.js"
dl "$VERS" "https://raw.githubusercontent.com/appendto/amplify" "lib/amplify.min.js"
dl "$VERS" "https://raw.githubusercontent.com/appendto/amplify" "lib/amplify.request.js"
dl "$VERS" "https://raw.githubusercontent.com/appendto/amplify" "lib/amplify.request.min.js"
dl "$VERS" "https://raw.githubusercontent.com/appendto/amplify" "lib/amplify.store.js"
dl "$VERS" "https://raw.githubusercontent.com/appendto/amplify" "lib/amplify.store.min.js"
VERS="1.1.0, 1.0a1, 1.0.0"
dl "$VERS" "https://raw.githubusercontent.com/appendto/amplify" "store/amplify.store.js"
dl "$VERS" "https://raw.githubusercontent.com/appendto/amplify" "request/amplify.request.js"
dl "$VERS" "https://raw.githubusercontent.com/appendto/amplify" "core/amplify.core.js"

pkgDled="$pkgDled somajs"
VERS="v2.1.0, v2.0.5, v2.0.4, v2.0.3, v2.0.2, v2.0.1, v2.0.0"
dl "$VERS" "https://raw.githubusercontent.com/somajs/somajs" "libs/infuse.js"
dl "$VERS" "https://raw.githubusercontent.com/somajs/somajs" "libs/soma-events.js"
VERS="v1.0.3, v1.0.2, v1.0.1, v1.0.0"
dl "$VERS" "https://raw.githubusercontent.com/somajs/somajs" "framework/src/soma.js"
curlThis soma_v1.0.3.min.js https://raw.githubusercontent.com/somajs/somajs/v1.0.3/framework/min/soma_v1.0.3_min.js
curlThis soma_v1.0.2.min.js https://raw.githubusercontent.com/somajs/somajs/v1.0.2/framework/min/soma_v1.0.2_min.js
curlThis soma_v1.0.1.min.js https://raw.githubusercontent.com/somajs/somajs/v1.0.1/framework/min/soma_v1.0.1_min.js
curlThis soma_v1.0.0.min.js https://raw.githubusercontent.com/somajs/somajs/v1.0.0/framework/min/soma_v1.0.0_min.js

pkgDled="$pkgDled knockoutjs"
VERS="v3.2.0, v3.1.0"
dl "$VERS" "https://raw.githubusercontent.com/knockout/knockout" "dist/knockout.js"
dl "$VERS" "https://raw.githubusercontent.com/knockout/knockout" "dist/knockout.debug.js"
VERS="v3.0.0, v2.3.0, v2.2.0, v2.1.0, v2.0.0, v1.2.1, v1.2.0, v1.1.2, v1.1.1, v1.1.0"
dl "$VERS" "https://raw.githubusercontent.com/knockout/knockout" "build/knockout-raw.js"
VERS="v1.0.5, v1.0.4, v1.0.3, v1.0.2, v1.0.1, v1.0.0"
dl "$VERS" "https://raw.githubusercontent.com/knockout/knockout" "build/knockout-debug.js"


echo "Done."
echo "Downloaded $dlFiles.  $totalFiles total files."
echo "The following exist:"
echo $pkgDled
