import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Dashboard from './pages/Dashboard';
import { Toaster } from 'sonner';
import './App.css';

function App() {
  const [token, setToken] = useState(localStorage.getItem('wallet_token'));
  const [user, setUser] = useState(null);

  useEffect(() => {
    const storedUser = localStorage.getItem('wallet_user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  const handleLogin = (authData) => {
    localStorage.setItem('wallet_token', authData.token);
    localStorage.setItem('wallet_user', JSON.stringify({
      userId: authData.userId,
      name: authData.name,
      email: authData.email
    }));
    setToken(authData.token);
    setUser({
      userId: authData.userId,
      name: authData.name,
      email: authData.email
    });
  };

  const handleLogout = () => {
    localStorage.removeItem('wallet_token');
    localStorage.removeItem('wallet_user');
    setToken(null);
    setUser(null);
  };

  return (
    <Router>
      <div className="App">
        <Toaster position="top-right" richColors />
        <Routes>
          <Route 
            path="/login" 
            element={!token ? <Login onLogin={handleLogin} /> : <Navigate to="/dashboard" />} 
          />
          <Route 
            path="/register" 
            element={!token ? <Register onLogin={handleLogin} /> : <Navigate to="/dashboard" />} 
          />
          <Route 
            path="/dashboard" 
            element={token ? <Dashboard user={user} onLogout={handleLogout} /> : <Navigate to="/login" />} 
          />
          <Route path="/" element={<Navigate to={token ? "/dashboard" : "/login"} />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;