import Image from "next/image";

export default function IconoTarea({ icon, description, styleBg = "bg-gray-200", size = "40" }) {

  const containerClasses = ` ${styleBg} rounded-3xl flex items-center justify-center shadow-md w-[${size}px] h-[${size}px]`;
  return (
    <div className={`flex flex-col w-[${size}px]`}>
      <div className={containerClasses}>
        <Image src={icon} alt="Icon" className={`w-auto h-auto m-8`} />
      </div>
      <p className="mt-2 text-4xl text-gray-700 text-center">{description}</p>
    </div>
  );
}
