The following does not work
```
clj -T:build uber
```
the following works
```
cd subproject && clj -T:build uber
```
