# hashiwokakero-puzzle
<p>An implementation of the logic puzzle Hashiwokakero (橋をかけろ Hashi o kakero; lit. "build bridges!") in Java. Other names of the logic puzzle include Bridges, Hashi, Ai-Ki-Ai or Chopsticks. The implementation was part of student project at the university of Hagen, Germany. A general outline of the features that the puzzle should have was given and every student had to come up with an implementation by herself/himself.</p>

<i>Example of a Hashiwokakero puzzle</i></br>
<img src="https://github.com/adi-wan/hashiwokakero-puzzle/blob/master/images/Example.png" width="475">


<h4>Goal and rules of Hashiwokakero</h4>

<p>In Hashiwokakero a number of circles is given. Inside each circle a number between 1 and 8 (inclusive) is drawn. The circles represent islands. The goal of the puzzle is to connect all islands by bridges (edges) so that (a) every island can be reached by every other island by using a path consisting of islands and bridges (i.e., the resulting graph must be a single connected component) and (b) every island is connected to other islands by exactly as many bridges as the number inside it dictates.</p>

<p>There are some rules for building bridges between islands (i.e., drawing edges between circles):
<ul>
  <li>The maximum number of bridges between two islands is 2 (also called a double bridge).</li>
  <li>A bridge must be vertical or horizontal.</li>
  <li>A bridge can neither cross an island nor another bridge.</li>
</ul></p>

<i>Screenshot of the main window of the application</i></br>
<img src="https://github.com/adi-wan/hashiwokakero-puzzle/blob/master/images/Controls.png" width="475">

<h4>Controls and options</h4>

<p>To <b>add a bridge</b> between 2 islands left-click on one of the islands next to its number in the direction the other island can be found, e.g., if you want to add a vertical bridge between two islands, click on the upper island below its number. <b>Removing a bridge</b> works like adding a bridge, simply use the right mouse button instead of the left one.</p>

<p>By checking the <b>"Anzahl fehlender Brücken anzeigen" checkbox</b>, the numbers inside the islands are changed so that a number does not represent the number of bridges an island requires in total but the number of bridges the island is missing at the moment, e.g., if an island requires 4 bridges and 4 briges have been built, the number of bridges the island is missing is 0 (highlighted by change of color from grey to green).</p>

<p>By clicking on the <b>"Nächste Brücke" button</b> a new bridge is added. If all bridges that have been added until the <b>"Nächste Brücke" button</b> is clicked are correct, i.e., lead to a correct solution, the bridge that is added, is also going to be correct. Otherwise this guarantee cannot be given. By clicking on the <b>"Automatisch lösen" button</b> the application is going to add bridges as if the "Nächste Brücke" button is clicked repeatedly (with pauses between clicks).</p>
