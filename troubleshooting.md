# For git commit -> git push -> RPC errors

- Open terminal and run:
git config --global http.version HTTP/1.1
git config --global http.postBuffer 524288000
git push
