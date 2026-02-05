curl --header "Content-type: application/json" \
--request POST \
--data '{"chat": "Hennings Privata chat", "message": {"text": "Hämligt meddelande :)", "user": {"name": "Coola Henning", "id": "1"}, "time": "2026-02-04T11:14:05Z"}}' \
http://localhost:2345/send_message