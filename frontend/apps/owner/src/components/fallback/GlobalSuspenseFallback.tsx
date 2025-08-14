const GlobalSuspenseFallback = () => {
  return (
    <div className="flex min-h-full items-center justify-center bg-gray-50">
      <div className="flex flex-col items-center space-y-4">
        <div className="h-12 w-12 animate-spin rounded-full border-4 border-gray-300 border-t-gray-900" />
        <div className="text-center">
          <p className="text-lg font-medium text-gray-900">로딩 중...</p>
          <p className="text-sm text-gray-600">잠시만 기다려주세요.</p>
        </div>
      </div>
    </div>
  );
};

export default GlobalSuspenseFallback;
