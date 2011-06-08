//My take on: http://solog.co/47/10-scala-one-liners-to-impress-your-friends/

//1. Multiple Each Item in a List by 2
//(1 to 10) map { _ * 2 }
(1 to 10) map (2*)


//2. Sum a List of Numbers
//(1 to 1000).reduceLeft( _ + _ )
(1 to 1000).sum


//3. Verify if Exists in a String
val words = List("scala", "akka", "play framework", "sbt", "typesafe")
val tweet = "This is an example tweet talking about scala and sbt."
//(words.foldLeft(false)( _ || tweet.contains(_) ))
wordList.exists(tweet.contains)


//4. Read in a File
//val fileText = io.Source.fromFile("data.txt").mkString
//val fileLines = io.Source.fromFile("data.txt").getLines.toList
val fileText = (io.Source fromFile "data.txt").mkString
val fileLines = (io.Source fromFile "data.txt").getLines.toList


//5. Happy Birthday to You!
//(1 to 4).map { i => "Happy Birthday " + (if (i == 3) "dear NAME" else "to You") }.foreach { println }
(1 to 4).map { i => "Happy Birthday " + (if (i == 3) "dear NAME" else "to You") }.mkString("\n")


//6. Filter list of numbers
//val (passed, failed) = List(49, 58, 76, 82, 88, 90) partition ( _ > 60 )
val (passed, failed) = List(49, 58, 76, 82, 88, 90) partition ( 60 < )


//7. Fetch and Parse an XML web service
//val results = XML.load("http://search.twitter.com/search.atom?&q=scala")
val results = XML load "http://search.twitter.com/search.atom?&q=scala"


//8. Find minimum (or maximum) in a List
//List(14, 35, -7, 46, 98).reduceLeft ( _ min _ )
//List(14, 35, -7, 46, 98).reduceLeft ( _ max _ )
List(14, 35, -7, 46, 98).min
List(14, 35, -7, 46, 98).max


//9. Parallel Processing
//val result = dataList.par.map(line => processItem(line))
val result = dataList.par.map(processItem _)


//10. Sieve of Eratosthenes
(n: Int) => (2 to n) |> 
(r => r.foldLeft(r.toSet)((ps, x) 
   => if (ps(x)) 
         ps -- (x * x to n by x) 
      else ps))