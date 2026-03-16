import React, { useState, useEffect } from 'react';
import { walletAPI, paymentAPI } from '../api';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '../components/ui/dialog';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import { toast } from 'sonner';
import { Wallet, Plus, Send, ArrowUpRight, ArrowDownRight, LogOut, RefreshCw, History } from 'lucide-react';

const Dashboard = ({ user, onLogout }) => {
  const [wallets, setWallets] = useState([]);
  const [selectedWallet, setSelectedWallet] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateWallet, setShowCreateWallet] = useState(false);
  const [showAddMoney, setShowAddMoney] = useState(false);
  const [showTransfer, setShowTransfer] = useState(false);

  const [addMoneyForm, setAddMoneyForm] = useState({ amount: '', description: '' });
  const [transferForm, setTransferForm] = useState({ toWalletId: '', amount: '', description: '' });

  useEffect(() => {
    fetchWallets();
  }, []);

  useEffect(() => {
    if (selectedWallet) {
      fetchTransactions(selectedWallet.id);
    }
  }, [selectedWallet]);

  const fetchWallets = async () => {
    try {
      const response = await walletAPI.getMyWallets();
      if (response.data.success) {
        setWallets(response.data.data);
        if (response.data.data.length > 0 && !selectedWallet) {
          setSelectedWallet(response.data.data[0]);
        }
      }
    } catch (error) {
      toast.error('Failed to fetch wallets');
    } finally {
      setLoading(false);
    }
  };

  const fetchTransactions = async (walletId) => {
    try {
      const response = await paymentAPI.getHistory(walletId);
      if (response.data.success) {
        setTransactions(response.data.data.content || []);
      }
    } catch (error) {
      console.error('Failed to fetch transactions', error);
    }
  };

  const handleCreateWallet = async () => {
    try {
      const response = await walletAPI.createWallet('USD');
      if (response.data.success) {
        toast.success('Wallet created successfully!');
        fetchWallets();
        setShowCreateWallet(false);
      }
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to create wallet');
    }
  };

  const handleAddMoney = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        walletId: selectedWallet.id,
        amount: parseFloat(addMoneyForm.amount),
        description: addMoneyForm.description,
        idempotencyKey: `add-${Date.now()}-${Math.random()}`
      };
      
      const response = await paymentAPI.addMoney(payload);
      if (response.data.success) {
        toast.success('Money added successfully!');
        fetchWallets();
        fetchTransactions(selectedWallet.id);
        setAddMoneyForm({ amount: '', description: '' });
        setShowAddMoney(false);
      }
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to add money');
    }
  };

  const handleTransfer = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        fromWalletId: selectedWallet.id,
        toWalletId: parseInt(transferForm.toWalletId, 10),
        amount: parseFloat(transferForm.amount),
        description: transferForm.description,
        idempotencyKey: `transfer-${Date.now()}-${Math.random()}`
      };
      
      const response = await paymentAPI.transfer(payload);
      if (response.data.success) {
        toast.success('Transfer successful!');
        fetchWallets();
        fetchTransactions(selectedWallet.id);
        setTransferForm({ toWalletId: '', amount: '', description: '' });
        setShowTransfer(false);
      }
    } catch (error) {
      toast.error(error.response?.data?.message || 'Transfer failed');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-950">
        <RefreshCw className="w-8 h-8 text-blue-500 animate-spin" />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-slate-950" data-testid="dashboard">
      <nav className="border-b border-slate-800 bg-slate-900/50 backdrop-blur-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center gap-3" data-testid="dashboard-header">
              <div className="p-2 bg-blue-500/10 rounded-lg">
                <Wallet className="w-6 h-6 text-blue-500" />
              </div>
              <h1 className="text-xl font-bold text-white">Digital Wallet</h1>
            </div>
            <div className="flex items-center gap-4">
              <span className="text-slate-300 text-sm" data-testid="user-name">Welcome, {user?.name}</span>
              <Button 
                variant="ghost" 
                size="sm" 
                onClick={onLogout}
                className="text-slate-400 hover:text-white"
                data-testid="logout-button"
              >
                <LogOut className="w-4 h-4 mr-2" />
                Logout
              </Button>
            </div>
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {wallets.length === 0 ? (
          <Card className="bg-slate-900/50 border-slate-800" data-testid="no-wallet-card">
            <CardHeader>
              <CardTitle className="text-white">No Wallet Found</CardTitle>
              <CardDescription className="text-slate-400">Create your first wallet to get started</CardDescription>
            </CardHeader>
            <CardContent>
              <Button 
                onClick={handleCreateWallet}
                className="bg-blue-600 hover:bg-blue-700"
                data-testid="create-first-wallet-button"
              >
                <Plus className="w-4 h-4 mr-2" />
                Create Wallet
              </Button>
            </CardContent>
          </Card>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-1 space-y-4">
              <Card className="bg-slate-900/50 border-slate-800" data-testid="wallet-card">
                <CardHeader>
                  <div className="flex justify-between items-center">
                    <CardTitle className="text-white">My Wallets</CardTitle>
                    <Button 
                      size="sm" 
                      onClick={handleCreateWallet}
                      className="bg-blue-600 hover:bg-blue-700"
                      data-testid="create-wallet-button"
                    >
                      <Plus className="w-4 h-4" />
                    </Button>
                  </div>
                </CardHeader>
                <CardContent className="space-y-3">
                  {wallets.map((wallet) => (
                    <div
                      key={wallet.id}
                      onClick={() => setSelectedWallet(wallet)}
                      className={`p-4 rounded-lg cursor-pointer transition-all ${
                        selectedWallet?.id === wallet.id
                          ? 'bg-blue-500/20 border border-blue-500/50'
                          : 'bg-slate-800/50 hover:bg-slate-800 border border-slate-700'
                      }`}
                      data-testid={`wallet-item-${wallet.id}`}
                    >
                      <div className="flex justify-between items-center">
                        <div>
                          <p className="text-slate-400 text-xs">Wallet #{wallet.id}</p>
                          <p className="text-2xl font-bold text-white">${Number(wallet.balance ?? 0).toFixed(2)}</p>
                          <p className="text-slate-500 text-xs mt-1">{wallet.currency}</p>
                        </div>
                        <div className={`px-2 py-1 rounded text-xs ${
                          wallet.status === 'ACTIVE' 
                            ? 'bg-green-500/20 text-green-400' 
                            : 'bg-red-500/20 text-red-400'
                        }`}>
                          {wallet.status}
                        </div>
                      </div>
                    </div>
                  ))}
                </CardContent>
              </Card>

              {selectedWallet && (
                <Card className="bg-slate-900/50 border-slate-800">
                  <CardHeader>
                    <CardTitle className="text-white">Quick Actions</CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-3">
                    <Dialog open={showAddMoney} onOpenChange={setShowAddMoney}>
                      <DialogTrigger asChild>
                        <Button 
                          className="w-full bg-green-600 hover:bg-green-700 justify-start"
                          data-testid="add-money-button"
                        >
                          <Plus className="w-4 h-4 mr-2" />
                          Add Money
                        </Button>
                      </DialogTrigger>
                      <DialogContent className="bg-slate-900 border-slate-800">
                        <DialogHeader>
                          <DialogTitle className="text-white">Add Money to Wallet</DialogTitle>
                          <DialogDescription className="text-slate-400">
                            Add funds to Wallet #{selectedWallet.id}
                          </DialogDescription>
                        </DialogHeader>
                        <form onSubmit={handleAddMoney} className="space-y-4" data-testid="add-money-form">
                          <div className="space-y-2">
                            <Label className="text-slate-300">Amount (USD)</Label>
                            <Input
                              type="number"
                              step="0.01"
                              placeholder="100.00"
                              value={addMoneyForm.amount}
                              onChange={(e) => setAddMoneyForm({ ...addMoneyForm, amount: e.target.value })}
                              required
                              className="bg-slate-800 border-slate-700 text-white"
                              data-testid="add-money-amount-input"
                            />
                          </div>
                          <div className="space-y-2">
                            <Label className="text-slate-300">Description (Optional)</Label>
                            <Input
                              type="text"
                              placeholder="Initial deposit"
                              value={addMoneyForm.description}
                              onChange={(e) => setAddMoneyForm({ ...addMoneyForm, description: e.target.value })}
                              className="bg-slate-800 border-slate-700 text-white"
                              data-testid="add-money-description-input"
                            />
                          </div>
                          <Button type="submit" className="w-full bg-green-600 hover:bg-green-700" data-testid="add-money-submit-button">
                            Add Money
                          </Button>
                        </form>
                      </DialogContent>
                    </Dialog>

                    <Dialog open={showTransfer} onOpenChange={setShowTransfer}>
                      <DialogTrigger asChild>
                        <Button 
                          className="w-full bg-blue-600 hover:bg-blue-700 justify-start"
                          data-testid="transfer-button"
                        >
                          <Send className="w-4 h-4 mr-2" />
                          Transfer Money
                        </Button>
                      </DialogTrigger>
                      <DialogContent className="bg-slate-900 border-slate-800">
                        <DialogHeader>
                          <DialogTitle className="text-white">Transfer Money</DialogTitle>
                          <DialogDescription className="text-slate-400">
                            Transfer from Wallet #{selectedWallet.id}
                          </DialogDescription>
                        </DialogHeader>
                        <form onSubmit={handleTransfer} className="space-y-4" data-testid="transfer-form">
                          <div className="space-y-2">
                            <Label className="text-slate-300">To Wallet ID</Label>
                            <Input
                              type="number"
                              placeholder="Enter wallet ID"
                              value={transferForm.toWalletId}
                              onChange={(e) => setTransferForm({ ...transferForm, toWalletId: e.target.value })}
                              required
                              className="bg-slate-800 border-slate-700 text-white"
                              data-testid="transfer-wallet-id-input"
                            />
                          </div>
                          <div className="space-y-2">
                            <Label className="text-slate-300">Amount (USD)</Label>
                            <Input
                              type="number"
                              step="0.01"
                              placeholder="50.00"
                              value={transferForm.amount}
                              onChange={(e) => setTransferForm({ ...transferForm, amount: e.target.value })}
                              required
                              className="bg-slate-800 border-slate-700 text-white"
                              data-testid="transfer-amount-input"
                            />
                          </div>
                          <div className="space-y-2">
                            <Label className="text-slate-300">Description (Optional)</Label>
                            <Input
                              type="text"
                              placeholder="Payment for services"
                              value={transferForm.description}
                              onChange={(e) => setTransferForm({ ...transferForm, description: e.target.value })}
                              className="bg-slate-800 border-slate-700 text-white"
                              data-testid="transfer-description-input"
                            />
                          </div>
                          <Button type="submit" className="w-full bg-blue-600 hover:bg-blue-700" data-testid="transfer-submit-button">
                            Transfer
                          </Button>
                        </form>
                      </DialogContent>
                    </Dialog>
                  </CardContent>
                </Card>
              )}
            </div>

            <div className="lg:col-span-2">
              {selectedWallet && (
                <Card className="bg-slate-900/50 border-slate-800" data-testid="transaction-history-card">
                  <CardHeader>
                    <div className="flex items-center gap-2">
                      <History className="w-5 h-5 text-blue-500" />
                      <CardTitle className="text-white">Transaction History</CardTitle>
                    </div>
                    <CardDescription className="text-slate-400">
                      Recent transactions for Wallet #{selectedWallet.id}
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    {transactions.length === 0 ? (
                      <p className="text-slate-400 text-center py-8" data-testid="no-transactions">No transactions yet</p>
                    ) : (
                      <div className="space-y-3">
                        {transactions.map((txn) => (
                          <div
                            key={txn.id}
                            className="flex items-center justify-between p-4 bg-slate-800/50 rounded-lg border border-slate-700"
                            data-testid={`transaction-${txn.id}`}
                          >
                            <div className="flex items-center gap-4">
                              <div className={`p-2 rounded-lg ${
                                txn.type === 'ADD_MONEY' 
                                  ? 'bg-green-500/20' 
                                  : txn.toWalletId === selectedWallet.id
                                  ? 'bg-green-500/20'
                                  : 'bg-red-500/20'
                              }`}>
                                {txn.type === 'ADD_MONEY' || txn.toWalletId === selectedWallet.id ? (
                                  <ArrowDownRight className="w-5 h-5 text-green-400" />
                                ) : (
                                  <ArrowUpRight className="w-5 h-5 text-red-400" />
                                )}
                              </div>
                              <div>
                                <p className="text-white font-medium">
                                  {txn.type === 'ADD_MONEY' 
                                    ? 'Money Added' 
                                    : txn.toWalletId === selectedWallet.id
                                    ? `Received from Wallet #${txn.fromWalletId}`
                                    : `Sent to Wallet #${txn.toWalletId}`
                                  }
                                </p>
                                <p className="text-slate-400 text-sm">{txn.description || 'No description'}</p>
                                <p className="text-slate-500 text-xs mt-1">
                                  {new Date(txn.createdAt).toLocaleString()}
                                </p>
                              </div>
                            </div>
                            <div className="text-right">
                              <p className={`text-lg font-bold ${
                                txn.type === 'ADD_MONEY' || txn.toWalletId === selectedWallet.id
                                  ? 'text-green-400'
                                  : 'text-red-400'
                              }`}>
                                {txn.type === 'ADD_MONEY' || txn.toWalletId === selectedWallet.id ? '+' : '-'}
                                ${txn.amount.toFixed(2)}
                              </p>
                              <div className={`inline-block px-2 py-0.5 rounded text-xs mt-1 ${
                                txn.status === 'SUCCESS' 
                                  ? 'bg-green-500/20 text-green-400' 
                                  : 'bg-red-500/20 text-red-400'
                              }`}>
                                {txn.status}
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </CardContent>
                </Card>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
