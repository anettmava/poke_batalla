import React, { useState, useEffect } from 'react';

const API_BASE_URL = 'http://localhost:8081/api/pokemon';

const Rectangles = () => {
  const [pokemonList, setPokemonList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [newPokemon, setNewPokemon] = useState({ name: '', level: 1 });
  const [battleResult, setBattleResult] = useState(null);
  const [selectedPokemon, setSelectedPokemon] = useState([]);

  // Fetch all Pokemon on component mount
  useEffect(() => {
    fetchPokemon();
  }, []);

  const fetchPokemon = async () => {
    try {
      setLoading(true);
      const response = await fetch(`${API_BASE_URL}/all`);
      if (!response.ok) {
        throw new Error('Failed to fetch Pokemon');
      }
      const data = await response.json();
      setPokemonList(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const createPokemon = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch(`${API_BASE_URL}/create?name=${encodeURIComponent(newPokemon.name)}&level=${newPokemon.level}`, {
        method: 'POST',
      });
      if (!response.ok) {
        throw new Error('Failed to create Pokemon');
      }
      const createdPokemon = await response.json();
      setPokemonList([...pokemonList, createdPokemon]);
      setNewPokemon({ name: '', level: 1 });
    } catch (err) {
      setError(err.message);
    }
  };

  const battlePokemon = async () => {
    if (selectedPokemon.length !== 2) {
      setError('Please select exactly 2 Pokemon to battle');
      return;
    }

    try {
      const response = await fetch(`${API_BASE_URL}/battle?pokemon1Id=${selectedPokemon[0]}&pokemon2Id=${selectedPokemon[1]}`, {
        method: 'POST',
      });
      if (!response.ok) {
        throw new Error('Failed to battle Pokemon');
      }
      const winnerId = await response.json();
      const winner = pokemonList.find(p => p.id === winnerId);
      setBattleResult(`${winner.name} wins the battle!`);
    } catch (err) {
      setError(err.message);
    }
  };

  const togglePokemonSelection = (pokemonId) => {
    setSelectedPokemon(prev =>
      prev.includes(pokemonId)
        ? prev.filter(id => id !== pokemonId)
        : prev.length < 2
        ? [...prev, pokemonId]
        : prev
    );
  };

  if (loading) return <div>Loading Pokemon...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div style={{ padding: '24px' }}>
      <h1 style={{ textAlign: 'center', color: '#d65252' }}>Pokemon Battle Arena</h1>

      {/* Create Pokemon Form */}
      <div style={{ marginBottom: '30px', textAlign: 'center' }}>
        <h2>Create New Pokemon</h2>
        <form onSubmit={createPokemon} style={{ display: 'inline-block' }}>
          <input
            type="text"
            placeholder="Pokemon Name"
            value={newPokemon.name}
            onChange={(e) => setNewPokemon({...newPokemon, name: e.target.value})}
            required
            style={{ marginRight: '10px', padding: '8px' }}
          />
          <input
            type="number"
            placeholder="Level"
            min="1"
            max="100"
            value={newPokemon.level}
            onChange={(e) => setNewPokemon({...newPokemon, level: parseInt(e.target.value)})}
            required
            style={{ marginRight: '10px', padding: '8px', width: '80px' }}
          />
          <button type="submit" style={{ padding: '8px 16px', backgroundColor: '#d65252', color: 'white', border: 'none', borderRadius: '4px' }}>
            Create Pokemon
          </button>
        </form>
      </div>

      {/* Battle Section */}
      {pokemonList.length >= 2 && (
        <div style={{ marginBottom: '30px', textAlign: 'center' }}>
          <h2>Battle Arena</h2>
          <p>Selected Pokemon: {selectedPokemon.length}/2</p>
          <button
            onClick={battlePokemon}
            disabled={selectedPokemon.length !== 2}
            style={{
              padding: '10px 20px',
              backgroundColor: selectedPokemon.length === 2 ? '#d65252' : '#ccc',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: selectedPokemon.length === 2 ? 'pointer' : 'not-allowed'
            }}
          >
            Battle!
          </button>
          {battleResult && (
            <div style={{ marginTop: '10px', fontSize: '18px', fontWeight: 'bold', color: '#d65252' }}>
              {battleResult}
            </div>
          )}
        </div>
      )}

      {/* Pokemon List */}
      <div style={{ display: 'flex', gap: '20px', justifyContent: 'center', flexWrap: 'wrap' }}>
        {pokemonList.map((pkmn) => (
          <div key={pkmn.id} style={{
            width: '280px',
            border: selectedPokemon.includes(pkmn.id) ? '3px solid #d65252' : '2px solid #444',
            borderRadius: '12px',
            padding: '18px',
            backgroundColor: selectedPokemon.includes(pkmn.id) ? '#ffe6e6' : '#fff5e6',
            boxShadow: '2px 4px 12px rgba(0,0,0,0.15)',
            cursor: 'pointer',
            transition: 'all 0.2s'
          }}
          onClick={() => togglePokemonSelection(pkmn.id)}
          >
            <h2 style={{ marginTop: 0, color: '#d65252' }}>{pkmn.name}</h2>
            
            <p style={{ marginTop: '12px', fontWeight: 'bold' }}>Level {pkmn.level}</p>
            {selectedPokemon.includes(pkmn.id) && (
              <div style={{ marginTop: '10px', color: '#d65252', fontWeight: 'bold' }}>
                SELECTED FOR BATTLE
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default Rectangles;