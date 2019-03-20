# hashiwokakero-puzzle
<p>An implementation of the logic puzzle Hashiwokakero (橋をかけろ Hashi o kakero; lit. "build bridges!") in Java. Other names of the logic puzzle include Bridges, Hashi, Ai-Ki-Ai or Chopsticks. The implementation was part of student project at the university of Hagen, Germany. A general outline of the features that the puzzle should have was given and every student had to come up with an implementation by herself/himself.</p>

<p>Example of a Hashiwokakero puzzle</p>
<img src="https://github.com/adi-wan/hashiwokakero-puzzle/blob/master/HashiwokakeroExample.png" width="475">

<h4>Goal and rules of Hashiwokakero</h4>
<p>In Hashiwokakero a number of circles is given. Inside each circle a number between 1 and 8 (inclusive) is drawn. The circles represent islands. The goal of the puzzle is to connect all islands by bridges (edges) so that (a) every island can be reached by every other island by using a path consisting of islands and bridges (i.e., the resulting graph must be a single connected component) and (b) every island is connected to other islands by exactly as many bridges as the number inside it dictates.</p>

<p>There are some rules for building bridges between islands (i.e., drawing edges between circles):
<ul>
  <li>The maximum number of bridges between two islands is 2 (also called a double bridge).</li>
  <li>A bridge must be vertical or horizontal.</li>
  <li>A bridge can neither cross an island nor another bridge.</li>
</ul></p>
