import React from 'react';
import './index.css';
import Login from './login';
import { createRoot } from 'react-dom/client';
const container = document.getElementById('Login');
const root = createRoot(container); // createRoot(container!) if you use TypeScript
root.render(<Login tab="home" />);


