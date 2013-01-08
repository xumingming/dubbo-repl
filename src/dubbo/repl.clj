(ns dubbo.repl
  (:use [dubbo.core])
  (:gen-class))

;; set the registry
(set-registry! "dubbo-test.china.alibaba.com:9090")

;; define the service methods we will call
(def-service-method "com.alibaba.china.marketing.service.FavoriteReadService"
  "1.0.0_xumm" "queryCollectedCount" ["long" "java.lang.String"] ["contentId" "contentType"])
(def-service-method "com.alibaba.china.marketing.service.FavoriteWriteService"
  "1.0.0_xumm" "addFavoriteItem" ["com.alibaba.china.marketing.param.FavoriteItemAddParam"] ["param"])
(def-service-method "com.alibaba.china.marketing.service.FavoriteWriteService"
  "1.0.0_xumm" "deleteFavoriteItem" ["com.alibaba.china.marketing.param.FavoriteItemDeleteParam"] ["param"])

(defn methods []
  (let [services (list-services)]
    (println)
    (doseq [service services]
      (println service)
      (let [methods (list-service-methods service)
            method-names (keys methods)]
        (doseq [method-name method-names
                :let [param-types (get-in methods [method-name :param-types])
                      param-names (get-in methods [method-name :param-names])]]
          (print (str "\t" method-name "("))
          (doseq [param-type param-types
                  param-name param-names]
            (print param-type " " param-name ", "))
          (println (str ")")))))))

;; the MAIN loop
#_(loop [input "help"]
  (try
    (let [argv (string/split input #" ")
          command (first argv)
          argv (rest argv)]
      (condp = command
        "set-config-id" (let [new-config-id (first argv)]
                          (set-config-id new-config-id)
                          (println "Config-id set to " new-config-id))
        
        "set-namespace" (let [new-namespace (first argv)]
                          (set-namespace new-namespace)
                          (println "namespace set to " new-namespace))
        
        "put"   (let [key (first argv)
                      value (second argv)
                      value-type (if (> (count argv) 2)
                                   (nth argv 2)
                                   nil)
                      value (condp = value-type
                              "int" (Integer/valueOf value)
                              "long" (Long/valueOf value)
                              value)
                      result-code (put @tair @tnamespace key value)]
                  (if (= (:code result-code) 0)
                    (println "SUCCESS!")
                    (println "FAIL! code:" (:code result-code) ", message:" (:message result-code))))
        
        "get" (let [key (first argv)
                      ret (get @tair @tnamespace key)]
                  (pprint/pprint ret))
        
        "delete" (let [key (first argv)
                       result-code (delete @tair @tnamespace key)]
                   (if (= (:code result-code) 0)
                     (println "SUCCESS!")
                     (println "FAIL! code:" (:code result-code) ", message:" (:message result-code))))
        
        "settings"    (env)

        "add-jar" (let [jar (first argv)]
                    (add-jar jar)
                    (println "Added jar" jar " to the classpath."))
        
        "exit" (System/exit 0)

        ;; if command is empty, do nothing
        ""  (print)
        
        (help)))
    (catch Throwable e
      (println "ERROR: " e)))

  (print (str " => "))
  (flush)
  (recur (read-line)))
