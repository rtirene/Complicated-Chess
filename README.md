# Complicated-Chess
A chess game, willfully more complicated.

__Rules:__

<h2>Gameplay</h2>

<p>There are two players: white and black, each with 8 pieces. White start and players alternate turns.</p>

<p>A <strong>turn</strong> consists in one of the following actions:</p>

<ul>
  <li>Moving a chosen piece of one's own side</li>
  <li>Attacking an opponent's piece in order to reduce its vitality</li>
  <li>Casting a spell</li>
</ul>

<p>Each piece type has different characteristics: the way it can move on the board, the way it can attack other pieces, its strength and its vitality. All those information can be accessed by keeping pressed on a piece.</p>

<p>If the vitality of a piece goes to 0, the piece is killed and removed from the board.</p>

<h3>Attack</h3>

<p>A piece can attack an opponent if the opponent is in range and there are no other pieces between them along the attack direction. When a pieced is attacked its vitality is diminished by the strength of the attacker. (Remember that mages and squire cannot attack)</p>

<h3>Combat</h3>

<p>If a move makes a piece arrive at a cell occupied by an opponent's piece, they start a combat: both pieces diminish their vitality by an amount equal to their opponent's strength; this is repeated until one or both pieces are killed.</p>

<h3>Spell</h3>

<p>Mages can cast spells. Every spell can only be cast once by a mage. No spell can involve a special cell or a cell where a mage lies. There are 4 kind of spell:</p>

<ul>
  <li>Heal: restore the initial vitality of a player's piece</li>
  <li>Teleport: move a selected piece to any cell that is not occupied by an ally.</li>
  <li>Freeze: a selected opponent's piece is frozen until the end of the opponent's 3rd turn. When frozen the piece cannot move, attack or deal damage in combat.</li>
  <li>Revive: bring back a player's killed piece with its initial vitality and unfrozen in its initial position (or one of the initial positions in the case of knight or squire)</li>
</ul>

<h2>Goal of the game</h2>

<p>The goal is to occupy three out of four special cells in the board with one's own pieces, or to kill all the opponent's pieces.</p>

]]>

__OS:__ Android

__Developed by:__
 - Francesco Foscarin
 - Rosilde Tatiana Irene
 - Giuseppe Testa
 - Alessandro Ragano
