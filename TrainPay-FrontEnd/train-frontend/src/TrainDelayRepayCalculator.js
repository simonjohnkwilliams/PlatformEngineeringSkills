import React, { useState, useEffect } from 'react';
import axios from 'axios';

const TrainDelayRepayCalculator = () => {
    const [fromStation, setFromStation] = useState('');
    const [toStation, setToStation] = useState('');
    const [time, setTime] = useState('');
    const [date, setDate] = useState('');
    const [error, setError] = useState(null);
    const [trainData, setTrainData] = useState([]);
    const [apiResponse, setApiResponse] = useState(null);
    const [sortKey, setSortKey] = useState('');
    const [sortDirection, setSortDirection] = useState('asc');

    useEffect(() => {
        document.title = "Delay Repay"; // Set the page title
    }, []);

    const handleSubmit = async (event) => {
        event.preventDefault();

        const stationMap = {
            'Godalming': 'GOD',
            'London Waterloo': 'WAT'
        };

        const requestData = {
            fromStation: stationMap[fromStation],
            toStation: stationMap[toStation],
            fromTime: time,
            toTime: time,
            toDate: date
        };

        try {
            const response = await axios.post('http://localhost:8080/api/tickets', requestData, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            console.log('Response:', response.data);
            setError(null); // Clear any previous errors
            setTrainData(response.data[date]); // Assuming the response is keyed by date
            setApiResponse(response.data); // Store the full API response
        } catch (error) {
            console.error('Error:', error);
            setError(error.message); // Set the error message
        }
    };

    // Function to redirect to the PayPal donation link
    const handleDonateClick = () => {
        window.open('https://www.paypal.com/donate/?hosted_button_id=YZH5VU88MUAU2', '_blank');
    };

    // Function to download the JSON data
    const handleDownloadJson = () => {
        if (!apiResponse) {
            alert("Please enter the train details to download and hit submit first.");
            return;
        }
        const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(apiResponse));
        const downloadAnchorNode = document.createElement('a');
        downloadAnchorNode.setAttribute("href", dataStr);
        downloadAnchorNode.setAttribute("download", "train_data.json");
        document.body.appendChild(downloadAnchorNode); // required for firefox
        downloadAnchorNode.click();
        downloadAnchorNode.remove();
    };

    const sortData = (key) => {
        const direction = sortKey === key && sortDirection === 'asc' ? 'desc' : 'asc';
        const sortedData = [...trainData].sort((a, b) => {
            if (a[0][key] > b[0][key]) return direction === 'asc' ? 1 : -1;
            if (a[0][key] < b[0][key]) return direction === 'asc' ? -1 : 1;
            return 0;
        });
        setSortKey(key);
        setSortDirection(direction);
        setTrainData(sortedData);
    };

    const filterData = (filter) => {
        const filteredData = trainData.filter(item => (item[0].delayTime > 5) === filter);
        setTrainData(filteredData);
    };

    const clearSortAndFilter = () => {
        setSortKey('');
        setSortDirection('asc');
        // Optionally, re-fetch or reset the train data to its original state if needed
    };

    const renderSortIcon = (key) => {
        if (sortKey !== key) return null;
        return sortDirection === 'asc' ? '▲' : '▼';
    };

    return (
        <div style={styles.container}>
            <div style={styles.header}>
                <h1>Train Delay Repay Calculator</h1>
                <p style={styles.headerText}>
                    Every day the train networks run late. It's a pain to find out if you are due a refund before you put in your refund claim.
                    This quick site uploads your ticket and tells you if you are due a refund and how to claim it.
                </p>
            </div>
            <div style={styles.content}>
                <div style={styles.formContainer}>
                    <form onSubmit={handleSubmit}>
                        <label>
                            From Station:
                            <select value={fromStation} onChange={(e) => setFromStation(e.target.value)} required>
                                <option value="">Select Station</option>
                                <option value="Godalming">Godalming</option>
                                <option value="London Waterloo">London Waterloo</option>
                            </select>
                        </label>
                        <br />
                        <label>
                            To Station:
                            <select value={toStation} onChange={(e) => setToStation(e.target.value)} required>
                                <option value="">Select Station</option>
                                <option value="Godalming">Godalming</option>
                                <option value="London Waterloo">London Waterloo</option>
                            </select>
                        </label>
                        <br />
                        <label>
                            Time:
                            <input type="time" value={time} onChange={(e) => setTime(e.target.value)} required />
                        </label>
                        <br />
                        <label>
                            Date:
                            <input type="date" value={date} onChange={(e) => setDate(e.target.value)} required />
                        </label>
                        <br />
                        <button type="submit">Submit</button>
                    </form>
                    {error && <div style={styles.errorNotice}>Error: {error}</div>}
                </div>
                <div style={styles.tableContainer}>
                    <table style={styles.table}>
                        <thead>
                            <tr>
                                <th style={styles.tableHeader} onClick={() => sortData('gbttPta')}>
                                    Train Time {renderSortIcon('gbttPta')}
                                    <span style={styles.clearIcon} onClick={clearSortAndFilter}>✖</span>
                                </th>
                                <th style={styles.tableHeader} onClick={() => sortData('delayTime')}>
                                    Amount Late {renderSortIcon('delayTime')}
                                    <span style={styles.clearIcon} onClick={clearSortAndFilter}>✖</span>
                                </th>
                                <th style={styles.tableHeader} onClick={() => filterData(true)}>
                                    Possible Claim
                                    <span style={styles.clearIcon} onClick={clearSortAndFilter}>✖</span>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            {Array.isArray(trainData) && trainData.length > 0 ? (
                                trainData.map((train, index) => (
                                    <tr key={index}>
                                        <td>{train[0].gbttPta || 'N/A'}</td>
                                        <td>{train[0].delayTime}</td>
                                        <td>{train[0].delayTime > 5 ? 'Yes' : 'No'}</td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="3">No data available</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            <div style={styles.footer}>
                <button style={styles.footerButton}>Suggest Updates</button>
                <button style={styles.footerButton} onClick={handleDonateClick}>
                    Donate
                </button>
                <button style={styles.footerButton} onClick={handleDownloadJson}>
                    Download JSON
                </button>
            </div>
        </div>
    );
};

// CSS-in-JS styles
const styles = {
    container: {
        fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
        background: 'linear-gradient(to bottom right, #6a11cb, #2575fc)',
        minHeight: '100vh',
        padding: '20px',
        color: '#333',
    },
    header: {
        backgroundImage: 'url("https://images.pexels.com/photos/72594/japan-train-railroad-railway-72594.jpeg?auto=compress&cs=tinysrgb&w=800")',
        backgroundSize: 'cover',
        color: 'white',
        textAlign: 'center',
        padding: '40px 0',
        marginBottom: '20px',
        borderRadius: '8px',
    },
    headerText: {
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
        padding: '10px',
        borderRadius: '8px',
    },
    content: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'flex-start',
        gap: '20px',
    },
    formContainer: {
        background: '#fff',
        padding: '20px',
        borderRadius: '8px',
        boxShadow: '0 4px 8px rgba(0,0,0,0.2)',
    },
    tableContainer: {
        width: '400px',
    },
    table: {
        width: '100%',
        borderCollapse: 'collapse',
        textAlign: 'left',
        background: '#fff',
        borderRadius: '8px',
        boxShadow: '0 4px 8px rgba(0,0,0,0.2)',
    },
    tableHeader: {
        borderBottom: '2px solid #ccc',
        padding: '8px',
        cursor: 'pointer',
        position: 'relative',
    },
    clearIcon: {
        marginLeft: '5px',
        cursor: 'pointer',
        color: 'red',
    },
    footer: {
        textAlign: 'center',
        marginTop: '20px',
    },
    footerButton: {
        marginRight: '10px',
        backgroundColor: '#6a11cb',
        color: 'white',
        border: 'none',
        padding: '10px 20px',
        borderRadius: '4px',
        cursor: 'pointer',
        transition: 'background-color 0.3s',
    },
    errorNotice: {
        color: 'red',
        marginTop: '10px',
    },
};

export default TrainDelayRepayCalculator;