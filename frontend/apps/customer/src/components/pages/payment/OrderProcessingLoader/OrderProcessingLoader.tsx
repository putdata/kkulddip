const OrderProcessingLoader = () => {
  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="text-center">
        <div className="mx-auto mb-4 h-12 w-12 animate-spin rounded-full border-b-2 border-blue-600"></div>
        <p className="text-lg font-medium">주문을 완료하는 중...</p>
      </div>
    </div>
  );
};

export default OrderProcessingLoader;
