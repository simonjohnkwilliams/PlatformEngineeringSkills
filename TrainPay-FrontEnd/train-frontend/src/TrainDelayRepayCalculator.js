import React, { Component } from 'react';

class TrainDelayRepayCalculator extends Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedImage: null,
        };
        this.fileInputRef = React.createRef();
    }

    handleButtonClick = () => {
        // Trigger the file input click event when the button is clicked
        this.fileInputRef.current.click();
    };

    handleImageChange = (event) => {
        const file = event.target.files[0];

        if (file) {
            const reader = new FileReader();

            // Load the image file as a Data URL to display it as a preview
            reader.onload = (e) => {
                this.setState({ selectedImage: e.target.result });
            };

            reader.readAsDataURL(file);
        }
    };

    render() {
        const { selectedImage } = this.state;

        return (
            <div>
                {/* Heading with a background image of a train */}
                <div
                    style={{
                        backgroundImage: 'url("https://images.pexels.com/photos/72594/japan-train-railroad-railway-72594.jpeg?auto=compress&cs=tinysrgb&w=800")',
                        backgroundSize: 'cover',
                        color: 'white',
                        textAlign: 'center',
                        padding: '40px 0',
                        marginBottom: '20px',
                    }}
                >
                    <h1>Train Delay Repay Calculator</h1>
                    <p style={{ backgroundColor: 'rgba(0, 0, 0, 0.5)', padding: '10px' }}>
                        Every day the train networks run late. It's a pain to find out if you are due a refund before you put in your refund claim.
                        This quick site uploads your ticket and tells you if you are due a refund and how to claim it. 
                    </p>
                </div>

                <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'flex-start', gap: '20px' }}>
                    {/* Image preview box */}
                    <div style={{ textAlign: 'center', width: '2in' }}>
                        <div
                            style={{
                                width: '2in',
                                height: '1in',
                                border: '2px solid #ccc',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                marginBottom: '10px',
                                overflow: 'hidden',
                                backgroundColor: '#f9f9f9',
                            }}
                        >
                            {selectedImage ? (
                                <img
                                    src={selectedImage}
                                    alt="Selected"
                                    style={{ maxWidth: '100%', maxHeight: '100%' }}
                                />
                            ) : (
                                <span style={{ color: '#aaa' }}>Image Preview</span>
                            )}
                        </div>

                        <button onClick={this.handleButtonClick}>
                            Upload Image
                        </button>

                        {/* Hidden file input for selecting an image */}
                        <input
                            type="file"
                            accept="image/*"
                            style={{ display: 'none' }}
                            ref={this.fileInputRef}
                            onChange={this.handleImageChange}
                        />
                    </div>

                    {/* Unpopulated table */}
                    <div style={{ width: '400px' }}>
                        <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                            <thead>
                                <tr>
                                    <th style={{ borderBottom: '2px solid #ccc', padding: '8px' }}>Train Provider</th>
                                    <th style={{ borderBottom: '2px solid #ccc', padding: '8px' }}>Train Time</th>
                                    <th style={{ borderBottom: '2px solid #ccc', padding: '8px' }}>Amount Late</th>
                                    <th style={{ borderBottom: '2px solid #ccc', padding: '8px' }}>Possible Claim</th>
                                </tr>
                            </thead>
                            <tbody>
                                {/* Table rows would go here */}
                            </tbody>
                        </table>
                    </div>
                </div>

                {/* Buttons below the image preview and table */}
                <div style={{ textAlign: 'center', marginTop: '20px' }}>
                    <button style={{ marginRight: '10px' }}>Suggest Updates</button>
                    <button>Donate</button>
                </div>
            </div>
        );
    }
}

export default TrainDelayRepayCalculator;
