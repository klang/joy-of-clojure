(ns joy-of-clojure.web
  (:import (com.sun.net.httpserver HttpHandler HttpExchange HttpServer)
	   (java.net InetSocketAddress HttpURLConnection)
	   (java.io IOException FilterOutputStream)
	   (java.util Arrays)))

(defn new-server [port path handler]
  (doto (HttpServer/create (InetSocketAddress. port) 0)
    (.createContext path handler)
    (.setExecutor nil)
    (.start)))

(defn default-handler [txt]
  (proxy [HttpHandler] []
    (handle [exchange]
      (.sendResponseHeaders exchange HttpURLConnection/HTTP_OK 0)
      (doto (.getResponseBody exchange)
	(.write (.getBytes txt))
	(.close)))))

(comment
  (def server (new-server 8123
			  "/joy/hello"
			  (default-handler "Hello Cleveland"))))

(.stop server 0)

(def p (default-handler
	 "There's no problem that can't be solved with another level of indirection"))

(def server (new-server 8123 "/joy/hello" p))

(defn make-handler-fn [fltr txt]
  (fn [this exchange]
    (let [b (.getBytes txt)]
      (-> exchange
	  .getResponseHeaders
	  (.set "Content-Type" "text/html"))
      (.sendResponseHeaders exchange HttpURLConnection/HTTP_OK 0)
      (doto (fltr (.getResponseBody exchange))
	(.write b)
	(.close)))))

(defn change-message
  "Convenience method to change a proxy's output message"
  ([p txt] (change-message p identity txt))
  ([p fltr txt]
     (update-proxy p {"handle" (make-handler-fn fltr txt)})))

(change-message p "Hello Dynamic!")

(defn screaming-filter [o]
  (proxy [FilterOutputStream] [o]
    (write [b]
	   (proxy-super write (.getBytes (str "<strong>"
					      (.toUpperCase (String. b))
					      "</strong>"))))))

(change-message p screaming-filter "whisper")