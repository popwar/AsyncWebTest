Job is restful endpoint.

Firstly, set tomcat server max threads(default 200) to a small number like 5.

Run sync fast job(1s) by “ab -n 300 -c 10 http://pathto/syncFastJob”, and at the same time, run sync slow job(5s) by “ab -n 100 -c 10 http://pathto/syncSlowJob”.
All 100 requests to slow job finished with 119.924s and all 300 requests to fast job finished with 160.628s.

Run sync fast job(1s) again. At the same time, run async job(5s) by “ab -n 100 -c 10 http://pathto/asyncSlowJob”. All 100 requests to slow job finished with 71.266s and all 300 requests to fast job finished with 60.388s.

When test sync slow job and async slow job alone. The former one took 50s to finish all 40 requests, and the latter one only took 39s

So in a highly concurrent env, better to use DefferredResult and CompletableFuture (faster than Observable).
