import React, { useState } from 'react';
import axios from 'axios';
import './App.css';

function App() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post('http://localhost:8882/login/login', {
                username,
                password,
            });

            // Extract user roles from the response
            const roles = response.data;

            // Handle roles accordingly (e.g., redirect based on role)
            if (roles.includes('DEVELOPER')) {
                // Redirect to developer dashboard
            } else if (roles.includes('SUPERUSER')) {
                // Redirect to superuser dashboard
            } else if (roles.includes('USER')) {
                // Redirect to user dashboard
            } else {
                // Handle unknown roles or other logic
            }
        } catch (error) {
            // Handle login failure
            console.error('Login failed', error.response.data);
        }
    };

    return (
    <div className="App">
            <h1>Login</h1>
            <form onSubmit={handleSubmit}>
                <label>
                    Username:
                    <input type="text" value={username} onChange={(e) => setUsername(e.target.value)} />
                </label>
                <br />
                <label>
                    Password:
                    <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
                </label>
                <br />
                <button type="submit">Login</button>
            </form>
            <br />
            <a href="http://localhost:8882/login/logout">Logout</a>
        </div>
    );
};

export default App;
