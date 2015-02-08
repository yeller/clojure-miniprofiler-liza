(ns clojure-miniprofiler.liza
  (:require [clojure-miniprofiler :as profile]
            [liza.store :as store]
            [liza.store.counters :as counters]))

(deftype ProfiledStore [original-bucket bucket-name]
  store/Bucket
  (store/get [_ k]
    (profile/custom-timing
     "store"
     "get"
     (str bucket-name " " k)
     (store/get original-bucket k)))

  (store/put [_ k v]
    (profile/custom-timing
     "store"
     "put"
     (str bucket-name " " k)
     (store/put original-bucket k v)))

  store/MergeableBucket
  (store/merge [_ v1 v2]
    (store/merge original-bucket v1 v2))

  store/ModifiableBucket
  (modify [_ k f]
    (profile/custom-timing
     "store"
     "modify"
     (str bucket-name " " k)
     (store/modify original-bucket k f)))

  store/DeleteableBucket
  (delete [b k]
    (profile/custom-timing
     "store"
     "delete"
     (str bucket-name " " k)
     (store/delete original-bucket k)))

  store/Wipeable
  (wipe [_]
    (profile/custom-timing
     "store"
     "wipe"
     "all"
     (store/wipe original-bucket)))

  counters/CounterBucket
  (counters/get-count [_ k]
    (profile/custom-timing
     "store"
     "get-count"
     (str bucket-name " " k)
     (counters/get-count original-bucket k)))

  (counters/increment [_ k n]
    (profile/custom-timing
     "store"
     "increment"
     (str bucket-name " " k)
     (counters/increment original-bucket k n))))

(defn profiled-store [original-store bucket-name]
  (ProfiledStore. original-store bucket-name))
