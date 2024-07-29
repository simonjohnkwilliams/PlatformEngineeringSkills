import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';

const TrainDelayRepayCalculator = () => {
    const [selectedImage, setSelectedImage] = useState(null);
    const fileInputRef = useRef();

    useEffect(() => {
        document.title = "Delay Repay"; // Set the page title
    }, []);

    const handleButtonClick = () => {
        // Trigger the file input click event when the button is clicked
        fileInputRef.current.click();
    };

    const handleImageChange = async (event) => {
        event.preventDefault();
        const file = event.target.files[0];

        if (file) {
            const reader = new FileReader();

            // Load the image file as a Data URL to display it as a preview
            reader.onload = (e) => {
                setSelectedImage(e.target.result);
            };

            reader.readAsDataURL(file);

            const formData = new FormData();
            formData.append('file', file); // Use `file` instead of `selectedImage`

            try {
                const response = await axios.post('/api/upload', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });
                console.log('File uploaded successfully', response.data);
            } catch (error) {
                console.error('Error uploading file', error);
            }
        }
    };

    // Function to redirect to the PayPal donation link
    const handleDonateClick = () => {
        window.open('https://www.paypal.com/donate/?hosted_button_id=YZH5VU88MUAU2', '_blank');
    };

    return (
        <div style={styles.container}>
            {/* Heading with a background image of a train */}
            <div style={styles.header}>
                <h1>Train Delay Repay Calculator</h1>
                <p style={styles.headerText}>
                    Every day the train networks run late. It's a pain to find out if you are due a refund before you put in your refund claim.
                    This quick site uploads your ticket and tells you if you are due a refund and how to claim it.
                </p>
            </div>

            <div style={styles.content}>
                {/* Image preview box */}
                <div style={styles.uploadCard}>
                    <div style={styles.imagePreview}>
                        {selectedImage ? (
                            <img
                                src={selectedImage}
                                alt="Selected"
                                style={styles.image}
                            />
                        ) : (
                            <span style={styles.imagePlaceholder}>Image Preview</span>
                        )}
                    </div>

                    <button style={styles.uploadButton} onClick={handleButtonClick}>
                        Upload Image
                    </button>

                    {/* Hidden file input for selecting an image */}
                    <input
                        type="file"
                        accept="image/*"
                        style={styles.fileInput}
                        ref={fileInputRef}
                        onChange={handleImageChange}
                    />
                </div>

                {/* Unpopulated table */}
                <div style={styles.tableContainer}>
                    <table style={styles.table}>
                        <thead>
                            <tr>
                                <th style={styles.tableHeader}>Train Provider</th>
                                <th style={styles.tableHeader}>Train Time</th>
                                <th style={styles.tableHeader}>Amount Late</th>
                                <th style={styles.tableHeader}>Possible Claim</th>
                            </tr>
                        </thead>
                        <tbody>
                            {/* Table rows would go here */}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Buttons below the image preview and table */}
            <div style={styles.footer}>
                <button style={styles.footerButton}>Suggest Updates</button>
                <button style={styles.footerButton} onClick={handleDonateClick}>
                    Donate
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
    uploadCard: {
        textAlign: 'center',
        width: '300px',
        padding: '20px',
        background: '#fff',
        borderRadius: '8px',
        boxShadow: '0 4px 8px rgba(0,0,0,0.2)',
    },
    imagePreview: {
        width: '100%',
        height: '150px',
        border: '2px solid #ccc',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        marginBottom: '10px',
        overflow: 'hidden',
        backgroundColor: '#f9f9f9',
        borderRadius: '8px',
    },
    image: {
        maxWidth: '100%',
        maxHeight: '100%',
    },
    imagePlaceholder: {
        color: '#aaa',
    },
    uploadButton: {
        backgroundColor: '#2575fc',
        color: 'white',
        border: 'none',
        padding: '10px 20px',
        borderRadius: '4px',
        cursor: 'pointer',
        transition: 'background-color 0.3s',
    },
    fileInput: {
        display: 'none',
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
};

export default TrainDelayRepayCalculator;
