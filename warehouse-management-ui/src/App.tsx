import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import InventoryManager from './pages/InventoryManager';
import './App.css';
import ErrorPage from './pages/ErrorPage';

function App() {


  return (
    <Router>
      <Routes>
      <Route path='/' element={<Login/>}/>
      <Route path='/dashboard' element={<Dashboard/>}/>
      <Route path='/inventory-manager' element={<InventoryManager/>}/> 
      <Route path='/error-page' element={<ErrorPage/>}/> 
      </Routes>
    </Router>
  );
}

export default App;
