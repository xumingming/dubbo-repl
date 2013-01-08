(ns dubbo.repl
  (:use [dubbo.core])
  (:gen-class))

;; set the registry
(set-registry! "dubbo-test.china.alibaba.com:9090")

;; define the service methods we will call
(def-service-method "com.alibaba.china.marketing.service.FavoriteReadService"
  "1.0.0_xumm" "queryCollectedCount" ["long" "java.lang.String"] ["contentId" "contentType"])
