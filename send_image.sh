curl -X POST http://localhost:2345/post_image/superimgaed \
--header "Content-type: application/json" \
-d "{\"image\":\"$(base64 -i 0000048B00000057.png | tr -d '\n')\"}"
